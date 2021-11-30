import React from "react";
import {Route, Routes} from "react-router-dom";

import RequiresAccount from "./component/guard/RequiresAccount";
import RequiresCharacter from "./component/guard/RequiresCharacter";
import MainNavigation from "./container/MainNavigation";

import Settings from "./route/Settings";
import Watches from "./route/Watches";

import {GlobalContextProvider, useGlobalContext} from "./hook/globalContext";

const App = () => {
    const globalContext = useGlobalContext();

    return <GlobalContextProvider value={globalContext}>
        <MainNavigation/>
        <div className="container mt-6">
            <Routes>
                <Route path="settings" element={
                    <RequiresAccount>
                        <Settings/>
                    </RequiresAccount>
                }/>
                <Route path="watches" element={
                    <RequiresCharacter>
                        <Watches/>
                    </RequiresCharacter>
                }/>
            </Routes>
        </div>
        <div className="footer mt-6">
            <div className="content has-text-centered">Copyright &copy; 2021 FFXIV Tools</div>
        </div>
    </GlobalContextProvider>;
};

export default App;
