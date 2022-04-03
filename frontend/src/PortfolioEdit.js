import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class PortfolioEdit extends Component {

    emptyItem = {
        imageUrl: "",
        title: "",
        description: "",
        twitterUserId: ""
    };

    constructor(props) {
        super(props);
        this.state = {
            item: this.emptyItem
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        const data = await (await fetch(`/portfolio/${this.props.match.params.id}`)).json();
        this.setState({item: data.portfolio});
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let item = {...this.state.item};
        item[name] = value;
        this.setState({item});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item} = this.state;

        await fetch(('/portfolio/' + this.props.match.params.id), {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
        });
        this.props.history.push('/');
    }

    render() {
        const {item} = this.state;
        const title = <h4>{'Edit Portfolio'}</h4>;

        return <div>
            <Container className="mt-5 bg-light">
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="imageUrl" className="mt-sm-4">Image URL</Label>
                        <Input required type="text" name="imageUrl" id="imageUrl" value={item.imageUrl}
                               onChange={this.handleChange} autoComplete="imageUrl" className="my-sm-1"/>
                    </FormGroup>
                    <FormGroup>
                        <Label for="title" className="mt-sm-4">Title</Label>
                        <Input required type="text" name="title" id="title" value={item.title}
                               onChange={this.handleChange} autoComplete="title" className="my-sm-1"/>
                    </FormGroup>
                    <FormGroup>
                        <Label for="description" className="mt-sm-4">Description</Label>
                        <Input required type="textarea" name="description" id="description" value={item.description}
                               onChange={this.handleChange} autoComplete="description" maxlength="255" className="my-sm-1"/>
                    </FormGroup>
                    <FormGroup>
                        <Label for="twitterUserId" className="mt-sm-4">Tweeter User ID</Label>
                        <Input required type="text" name="twitterUserId" id="twitterUserId" value={item.twitterUserId}
                               onChange={this.handleChange} autoComplete="twitterUserId" className="my-sm-1"/>
                    </FormGroup>
                    <FormGroup className="mt-sm-5">
                        <Button color="primary" type="submit">Save</Button>{' '}
                        <Button color="secondary" tag={Link} to="/">Cancel</Button>
                    </FormGroup>
                </Form>
            </Container>
        </div>
    }
}
export default withRouter(PortfolioEdit);