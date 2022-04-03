import React, { Component } from 'react';
import {
    Button,
    Card,
    CardBody,
    CardImg,
    CardSubtitle,
    CardText,
    CardTitle,
    Col,
    Container,
    Row,
    Table
} from "reactstrap";
import {Link} from "react-router-dom";

class PortfolioList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            imageUrl: "",
            title: "",
            description: "",
            tweets: [],
            errorMessage: ""
        };
    }

    componentDidMount() {
        fetch('/portfolio/118')
            .then(response => response.json())
            .then(data => {
                if (data.portfolio != null) {
                    this.setState({imageUrl: data.portfolio.imageUrl, title: data.portfolio.title,
                        description: data.portfolio.description, tweets: data.portfolio.tweets})
                } else {
                    this.setState({errorMessage: data.error.message})
                }
            });
    }

    render() {
        const hasError = this.state.errorMessage;
        if (hasError) {
            return (
                <Container>
                    <h5>An error occurred: {this.state.errorMessage}</h5>
                </Container>
            );
        }

        const tweets = this.state.tweets.map(tweet => {
            return <tr key={tweet.id}>
                <td className="align-middle">{tweet.author_id ? tweet.author_id : this.state.title}</td>
                <td className="align-middle">{tweet.text}</td>
            </tr>
        });

        return (
            <Container className="mt-5 bg-light">
                <Row>
                    <Col sm={{
                        offset: 1,
                        size: 4
                    }}>
                        <Card>
                            <CardImg src={this.state.imageUrl} style={{width:"30%", height:"60%", position: "center"}} className="mx-auto" />
                            <CardBody>
                                <hr/>
                                <CardTitle tag="h5" align="center">{this.state.title + "`s Timeline"} </CardTitle>
                                <br/>
                                <CardText>
                                    <Table bordered hover style={{backgroundColor: "#E0FFFF"}}>
                                        <thead>
                                        <tr>
                                            <th className="text-center">Author</th>
                                            <th className="text-center">Description</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {tweets}
                                        </tbody>
                                    </Table>
                                </CardText>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm={{
                        offset: 1,
                        size: 4
                    }}>
                        <Card>
                            <CardTitle tag="h2" align="center">{this.state.title}</CardTitle>
                            <hr/>
                            <CardSubtitle tag="h5" align="center">My Work Experience</CardSubtitle>
                            <br/>
                            <CardBody>{this.state.description}</CardBody>
                            <Button style={{backgroundColor: "#3CB371"}} tag={Link} to={"/portfolio/118"}>Edit Portfolio</Button>
                        </Card>
                    </Col>
                </Row>
            </Container>
        );
    }
}
export default PortfolioList;