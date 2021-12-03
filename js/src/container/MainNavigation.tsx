import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";

import Icon from "../component/Icon";

import {useAccount} from "../hook/globalContext";

const MainNavigation = () => {
    const [account] = useAccount();
    const [isOpen, setIsOpen] = useState(false);
    const [menuActive, setMenuActive] = useState(false);

    useEffect(() => {
        const onDocumentClick = () => {
            setIsOpen(false);
            setMenuActive(false);
        };
        document.addEventListener("click", onDocumentClick);
        return () => document.removeEventListener("click", onDocumentClick);
    }, []);

    let navbarEnd;
    if (account) {
        navbarEnd = <div className={`navbar-item has-dropdown ${menuActive ? "is-active" : ""}`}>
            <div
                children={account.name || "New User"}
                className="navbar-link"
                onClick={e => {
                    e.stopPropagation();
                    setMenuActive(!menuActive);
                }}
            />
            <div className="navbar-dropdown">
                <Link className="navbar-item" to="/settings">Settings</Link>
                <hr className="navbar-divider"/>
                <a className="navbar-item" href="/auth/logout">Log Out</a>
            </div>
        </div>;
    } else {
        navbarEnd = <div className="navbar-item">
            <div className="buttons">
                <a href="/auth/redirect" className="button is-primary">
                    <Icon name="sign-in-alt"/>
                    <span>Sign In</span>
                </a>
            </div>
        </div>;
    }

    return (
        <nav className="navbar is-fixed-top has-shadow" role="navigation" aria-label="main navigation">
            <div className="navbar-brand">
                <Link to="/" className="navbar-item">FFXIV Tools</Link>
                <a
                    aria-expanded={isOpen}
                    aria-label="menu"
                    className={`navbar-burger ${isOpen ? "is-active" : ""}`}
                    onClick={e => {
                        e.stopPropagation();
                        setIsOpen(!isOpen);
                    }}
                    role="button"
                >
                    <span aria-hidden="true"/>
                    <span aria-hidden="true"/>
                    <span aria-hidden="true"/>
                </a>
            </div>
            <div className={`navbar-menu ${isOpen ? "is-active" : ""}`}>
                <div className="navbar-start">
                    <Link to="/watches" className="navbar-item">Watched Items</Link>
                </div>
                <div className="navbar-end">{navbarEnd}</div>
            </div>
        </nav>
    );
}

export default MainNavigation;
