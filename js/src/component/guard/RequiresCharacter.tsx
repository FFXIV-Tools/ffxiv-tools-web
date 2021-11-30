import React from "react";
import {Link} from "react-router-dom";

import RequiresAccount from "./RequiresAccount";

import {useAccount} from "../../hook/globalContext";

const Guard = ({children}: { children: JSX.Element }) => {
    const [account] = useAccount();
    if (!account?.name || !account?.datacenterId || !account?.worldId) {
        return <p className="has-text-centered">Visit <Link to="/settings">user settings</Link> to finish setting up
            your account to load price information.</p>;
    }
    return children;
};

const RequiresCharacter = ({children}: { children: JSX.Element }) => (
    <RequiresAccount>
        <Guard>{children}</Guard>
    </RequiresAccount>
);

export default RequiresCharacter;
