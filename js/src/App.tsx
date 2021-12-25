import React from "react";
import {Link, Route, Routes} from "react-router-dom";

import RequiresAccount from "./component/guard/RequiresAccount";
import RequiresCharacter from "./component/guard/RequiresCharacter";
import MainNavigation from "./container/MainNavigation";

import Currencies from "./route/market/Currencies";
import Settings from "./route/Settings";
import Watches from "./route/Watches";

import {GlobalContextProvider, useGlobalContext} from "./hook/globalContext";

const App = () => {
    const globalContext = useGlobalContext();

    return <GlobalContextProvider value={globalContext}>
        <MainNavigation/>
        <div className="container mt-6">
            <Routes>
                <Route index element={
                    <RequiresAccount>
                        <p className="has-text-centered">Yeah there's nothing actually on this page, go to <Link
                            to="/watches">watched items</Link> since it's the only thing available on this site so far.
                        </p>
                    </RequiresAccount>
                }/>
                <Route path="market/currencies" element={
                    <RequiresCharacter>
                        <Currencies/>
                    </RequiresCharacter>
                }/>
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
