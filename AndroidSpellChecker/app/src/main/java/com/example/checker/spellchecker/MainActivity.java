package com.example.checker.spellchecker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity {
    private EditText editor;
    private Button check;
    private ScrollView resultContainer;
    private LinearLayout resultContent;

    private JSONObject checkResult;

    private String modifiedText;
    private String uglyResult;

    private static final String LINE = "\n";
    private static final String STARS = "**********\n";

    // default example
    private static final String test = "I {0}is writing a Java {1}prject.\n" +
            "**********\n" +
            "{0}is: am, was\n" +
            "{1}prject: project";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editor = (EditText) findViewById(R.id.editor);
        check = (Button) findViewById(R.id.check);
        resultContainer = (ScrollView) findViewById(R.id.result_container);
        resultContent = (LinearLayout) findViewById(R.id.result_content);
        resultContainer.setVisibility(View.GONE);

        // default example
        editor.setText("I is writing a Java prject.");
    }

    public void checkSpell(View view) {
        resultContainer.setVisibility(View.VISIBLE);

        // TODO: Computer IP has to be changed to the localhost computer's IP address
        String url = "http://10.0.0.8:8080/get/correction";
        // get check result from api
        // returned result is a long string with flagged original text and the lists of correct suggestions, divided by 10 *'s and a \n
        CheckResultUtil.getCheckResult(editor.getText().toString(), url,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        final String uglyResult = modifyText(test);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processCheckResult(uglyResult);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String uglyResult = modifyText(response.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processCheckResult(uglyResult);
                            }
                        });
                    }
                });
    }

    // process the returned long string into text part and correct suggestion part
    public String modifyText(String uglyResponse) {
        String[] split = uglyResponse.split(LINE);
        modifiedText = split[0];
        uglyResult = uglyResponse.substring(uglyResponse.indexOf(STARS) + STARS.length()).trim();
        return uglyResult;
    }

    // process the second suggestion part into a JASONObject
    private JSONObject resultSplit(String uglyResult) {
        JSONObject result = null;
        String[] checkSplit = uglyResult.split(LINE);
        try {
            result = new JSONObject();
            for (String s : checkSplit) {
                String name = s.split(":")[0].trim();
                String suggestionArrayString = s.split(":")[1].trim();
                String[] suggestionSplit = suggestionArrayString.split(",");
                JSONArray suggestion = new JSONArray();
                suggestion.put(name.substring(3));
                for (int j = 0; j < suggestionSplit.length; j++) {
                    suggestion.put(suggestionSplit[j]);
                }
                result.put(name, suggestion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void processCheckResult(String uglyResult) {
        checkResult = resultSplit(uglyResult);
        markText(checkResult);
        addCheckSuggestion(checkResult);
    }

    // mark the wrong words into red
    private void markText(JSONObject checkResult) {
        try {
            String text = editor.getText().toString();
            ArrayList<String> targetList = new ArrayList<>();
            for (int i = 0; i < checkResult.length(); i++) {
                targetList.add(checkResult.names().getString(i));
            }
            editor.setText(getSpannableStringBuilder(modifiedText, targetList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static SpannableStringBuilder getSpannableStringBuilder(String text, ArrayList<String> texts) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        if (texts != null) {
            for (int i = 0; i < texts.size(); i++) {
                String value = texts.get(i);
                if (!TextUtils.isEmpty(value) && text.contains(value)) {
                    int startIndex = text.indexOf(value);
                    int entIndex = startIndex + value.length();
                    if (entIndex > startIndex && entIndex <= text.length()) {
                        builder.setSpan(new ForegroundColorSpan(Color.RED), startIndex, entIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        return builder;
    }

    // add suggestion word to each wrong word
    private void addCheckSuggestion(JSONObject checkResult) {
        TextView title = new TextView(this);
        title.setText("Suggestion");
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setTextSize(24);
        resultContent.addView(title);

        try {
            JSONArray names = checkResult.names();
            for (int i = 0; i < names.length(); i++) {
                String target = names.getString(i);
                JSONArray suggestion = checkResult.getJSONArray(target);
                View item = createSuggestionItem(target, suggestion);
                resultContent.addView(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // list suggestions for all wrong word and create replace button for each suggestion option
    private View createSuggestionItem(final String text, final JSONArray suggestion) {
        final LinearLayout container = new LinearLayout(this);
        try {
            container.setOrientation(LinearLayout.VERTICAL);
            container.setGravity(Gravity.CENTER_HORIZONTAL);
            TextView targetTextView = new TextView(this);
            targetTextView.setText(text);
            targetTextView.setGravity(Gravity.LEFT);
            targetTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            targetTextView.setTextSize(20);
            container.addView(targetTextView);

            for (int i = 0; i < suggestion.length(); i++) {
                final String suggestString = suggestion.getString(i);
                LinearLayout suggestionLine = new LinearLayout(this);
                TextView suggestText = new TextView(this);
                suggestText.setTextSize(18);
                suggestText.setText(suggestString);
                LinearLayout.LayoutParams suggestTextLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                suggestText.setLayoutParams(suggestTextLp);
                suggestionLine.addView(suggestText);

                final Button replaceButton = new Button(this);
                replaceButton.setText("replace");
                // replace with the selected suggestion when clicking the replace button beside the suggestion
                replaceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replaceText(text, suggestString);
                        container.setVisibility(View.GONE);
                    }
                });
                suggestionLine.addView(replaceButton);
                container.addView(suggestionLine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return container;
    }

    // replace the wrong word in the original text
    private void replaceText(String target, String suggest) {
        modifiedText = modifiedText.replace(target, suggest);
        editor.setText(modifiedText);
        markText(checkResult);
    }
}