import React, { Component } from 'react';
import './App.css';
import axios from 'axios';

// the backend url
const url = "http://localhost:8080/get/correction";

class App extends Component {

  state = {
    input: "",
    output: "",
    standard: ""
  }

  onChangeHandler = (e) => {
    this.setState({
      input: e.target.value
    });
  }

  onClearHandler = () => {
    this.setState({
      input: "",
      output: "",
      standard: ""
    })
  }

  onSubmitHandler = () => {
    const input = {
      "text": this.state.input
    }
    console.log(input);
    axios.post(url, input)
      .then(res => {
        this.setState({
          output: res.data.output,
          standard: res.data.standard
        });
      })
      .catch(err => {
        console.log(err);
      });
  }

  render() {
    return (
      <div className="App">
        <div className="container my-4 pb-2">
          <h1>Language Corrector - Group4</h1>
        </div>
        <div className="container">
          <div className="row">
            <div className="col-4">
              <div className="form-group">
                <h4>Input</h4>
                <textarea className="form-control" id="exampleFormControlTextarea1" rows="18" value={this.state.input} onChange={this.onChangeHandler}></textarea>
              </div> 
            </div>
            <div className="col-4">
              <div className="form-group">
                <h4>Output</h4> 
                <textarea className="form-control" id="exampleFormControlTextarea1" rows="18" readOnly value={this.state.output}></textarea>
              </div> 
            </div>
            <div className="col-4">
              <div className="form-group">
                <h4>Standard</h4> 
                <textarea className="form-control" id="exampleFormControlTextarea1" rows="18" readOnly value={this.state.standard}></textarea>
              </div> 
            </div>
          </div>
          <div className="btn btn-primary btn-m my-3 mx-5" onClick={this.onSubmitHandler}>Check</div>
          <div className="btn btn-primary btn-m my-3 mx-5" onClick={this.onClearHandler}>Clear</div>
        </div> 
      </div>
    );
  }
}

export default App;
