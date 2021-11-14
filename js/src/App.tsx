import React, {useEffect, useState} from "react";

import CardModal from "./component/modal/CardModal";
import Dropdown from "./component/Dropdown";

import {useLoginStatus} from "./hook/auth";
import {authToken} from "./util/cookie";

const toPercentString = (value: number, decimals: number = 2): string =>
    `${(value * 100).toFixed(decimals)}%`;

const fetchWatches = async (): Promise<Watch[]> => {
    const response = await fetch("/api/v1/watches", {
        headers: {
            Authorization: `Bearer ${authToken()}`,
        },
    });

    return await response.json();
};

type WatchItemProps = {
    watch: Watch,
};

const WatchItem = ({watch}: WatchItemProps) => {
    const [materialModalActive, setMaterialModalActive] = useState<boolean>(false);

    function hideMaterialModal() {
        setMaterialModalActive(false);
    }

    function showMaterialModal() {
        setMaterialModalActive(true);
    }

    const itemMin = watch.worldMinimum;
    const itemMax = Math.round(watch.worldMean + watch.worldDeviation);
    const materialsMin = watch.materials.reduce((sum: number, material: Material) =>
        sum + material.datacenterMinimum * material.quantity, 0);
    const materialsMax = Math.round(watch.materials.reduce((sum: number, material: Material) =>
        sum + (material.datacenterMean + material.datacenterDeviation) * material.quantity, 0));

    const minProfit = itemMin - materialsMax;
    const maxProfit = itemMax - materialsMin;
    const minProfitPercent = toPercentString((itemMin - materialsMax) / itemMin);
    const maxProfitPercent = toPercentString((itemMax - materialsMin) / itemMax);

    let materialsDropdownItem;
    if (watch.materials.length) {
        materialsDropdownItem = <>
            <div className="dropdown-item" role="menuitem" onClick={showMaterialModal}>Materials</div>
            <hr className="dropdown-divider"/>
        </>;
    }

    return <tr key={watch.itemId}>
        <td>
            <Dropdown label="Actions">
                <>
                    {materialsDropdownItem}
                    <div className="dropdown-item" role="menuitem">Delete</div>
                </>
            </Dropdown>
            <CardModal active={materialModalActive} close={hideMaterialModal} title={`Materials for ${watch.name}`}>
                <table className="table is-bordered is-striped">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Unit Minimum</th>
                        <th>Unit Maximum</th>
                        <th>Quantity</th>
                    </tr>
                    </thead>
                    <tbody>
                    {watch.materials.map(material => {
                        const materialMin = material.datacenterMinimum;
                        const materialMax = Math.round(material.datacenterMean + material.datacenterDeviation);

                        return <tr key={material.itemId}>
                            <td>{material.name}</td>
                            <td>{materialMin.toLocaleString()}</td>
                            <td>{materialMax.toLocaleString()}</td>
                            <td>{material.quantity}</td>
                        </tr>
                    })}
                    </tbody>
                </table>
            </CardModal>
        </td>
        <td>{watch.name}</td>
        <td>{itemMin.toLocaleString()}</td>
        <td>{itemMax.toLocaleString()}</td>
        <td>{materialsMin.toLocaleString()}</td>
        <td>{materialsMax.toLocaleString()}</td>
        <td>{minProfit.toLocaleString()} ({minProfitPercent})</td>
        <td>{maxProfit.toLocaleString()} ({maxProfitPercent})</td>
    </tr>;
};

const WatchList = () => {
    const [watches, setWatches] = useState<Watch[]>([]);

    useEffect(() => {
        (async () => setWatches(await fetchWatches()))();
    }, []);

    return <>
        <main className="content">
            <table className="table is-striped">
                <thead>
                <tr>
                    <th style={{width: "1px"}}/>
                    <th>Item Name</th>
                    <th><em data-tooltip="Lamia Price">Item Min</em></th>
                    <th><em data-tooltip="Lamia Price">Item Max</em></th>
                    <th><em data-tooltip="Datacenter Price">Materials Min</em></th>
                    <th><em data-tooltip="Datacenter Price">Materials Max</em></th>
                    <th>Profit Min</th>
                    <th>Profit Max</th>
                </tr>
                </thead>
                <tbody>
                {watches.map(watch => <WatchItem key={watch.itemId} watch={watch}/>)}
                </tbody>
            </table>
        </main>
    </>;
}

const App = () => {
    const [loggedIn, logOut] = useLoginStatus();

    let content;
    let navbarButtons;
    if (loggedIn) {
        navbarButtons = <div className="buttons">
            <button className="button is-primary" onClick={logOut}>Log Out</button>
        </div>;

        content = <WatchList/>;
    } else {
        navbarButtons = <div className="buttons">
            <a href="/auth/redirect" className="button is-primary">Log In</a>
        </div>;

        content = <p className="has-text-centered">Please log in to use features on this site.</p>;
    }

    return <>
        <nav className="navbar is-fixed-top has-shadow" role="navigation" aria-label="main navigation">
            <div className="navbar-brand">
                <a href="/" className="navbar-item">FFXIV Tools</a>
            </div>
            <div className="navbar-menu">
                <div className="navbar-start">
                    <a href="/" className="navbar-item">Watched Items</a>
                </div>
                <div className="navbar-end">
                    <div className="navbar-item">{navbarButtons}</div>
                </div>
            </div>
        </nav>
        <div className="container mt-5">{content}</div>
        <div className="footer mt-6">
            <div className="content has-text-centered">Copyright &copy; 2021 FFXIV Tools</div>
        </div>
    </>;
};

export default App;
