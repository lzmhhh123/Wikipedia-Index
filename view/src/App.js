import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import { Theme as UWPThemeProvider, getTheme } from 'react-uwp/Theme';
import { TextBox, Icon, ListView } from 'react-uwp';
import axios from 'axios';

class App extends Component {
  constructor() {
    super();
    this.state = {
      table: undefined,
      searchable: false
    }
    this.search = this.search.bind(this);
  }
  search(event) {
    if (event.key !== 'Enter') return;
    let value = this.refs.input.getValue();
    console.log(value);
    axios.post('/search', {value: value})
      .then(res => {
        this.setState({table: res.data.table, searchable: true})
      })
  }
  render() {
    // console.log(this.state);
    return (
      <UWPThemeProvider
        theme={getTheme({
          themeName: "dark",
          accent: "rgb(1, 143, 255)",
          useFluentDesign: true,
        })}
      >
        <div className="App">
          <img src="/logo.png" style={{marginLeft: 'auto', marginRight: 'auto'}} />
          <TextBox
            style={{width: "600px", marginLeft: 'auto', marginRight: 'auto', marginTop: "20px"}}
            background="none"
            placeholder="Search keyword for Wikipedia pages."
            leftNode={<Icon style={{margin: '0 16px'}} >Search</Icon>}
            ref="input"
            onKeyPress={this.search}
          />
          {
            this.state.searchable ? (this.state.table !== undefined ? <ListView
              style={{width: 600, marginTop: 20}}
              background="none"
              listSource={this.state.table.map((s, index) => {
                return <div key={index} onClick={() => window.open(`/page/${s}`)}>
                  {`page id: ${s}`}
                </div>
              })}
            /> : <ListView style={{width: 600, marginTop: 20}} background="none" listSource={Array(1).fill(0).map((number, index) => <div disabled key={index}>{"No results."}</div>)} />) : null
          }
        </div>
      </UWPThemeProvider>
    );
  }
}

export default App;
