import React, { Component } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import PortfolioList from './PortfolioList';
import PortfolioEdit from "./PortfolioEdit";

class App extends Component {
  render() {
    return (
        <Router>
          <Switch>
            <Route path='/' exact={true} component={PortfolioList}/>
            <Route path='/portfolio/:id' component={PortfolioEdit}/>
          </Switch>
        </Router>
    )
  }
}

export default App;