import React from "react";

import {useAccount} from "../../hook/globalContext";

const RequiresAccount = ({children}: { children: JSX.Element }) => {
    const [account] = useAccount();
    if (!account) {
        return <p className="has-text-centered">
            Please <a href="/auth/redirect">sign in</a> to use this feature.
        </p>;
    }
    return children;
};

export default RequiresAccount;
