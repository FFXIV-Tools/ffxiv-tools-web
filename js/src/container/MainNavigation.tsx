import React from "react";

import Icon from "../component/Icon";

import {isLoggedIn} from "../util/cookie";

const MainNavigation = () => {
    let logInOut;
    if (isLoggedIn()) {
        logInOut = <div className="buttons">
            <a href="/auth/logout" className="button is-primary">
                <Icon name="sign-out-alt"/>
                <span>Sign Out</span>
            </a>
        </div>;
    } else {
        logInOut = <div className="buttons">
            <a href="/auth/redirect" className="button is-primary">
                <Icon name="sign-in-alt"/>
                <span>Sign In</span>
            </a>
        </div>;
    }

    return (
        <nav className="navbar is-fixed-top has-shadow" role="navigation" aria-label="main navigation">
            <div className="navbar-brand">
                <a href="/" className="navbar-item">FFXIV Tools</a>
            </div>
            <div className="navbar-menu">
                <div className="navbar-start">
                    <a href="/" className="navbar-item">Watched Items</a>
                </div>
                <div className="navbar-end">
                    <div className="navbar-item">{logInOut}</div>
                </div>
            </div>
        </nav>
    );
}

export default MainNavigation;
