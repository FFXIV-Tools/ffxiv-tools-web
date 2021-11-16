import React, {useEffect, useState} from "react";

import {getSearchResults} from "./action/search";
import {createWatch, deleteWatch, getWatches} from "./action/watch";

import {useLoginStatus} from "./hook/auth";
import {useModal} from "./hook/modal";

import CardModal from "./component/modal/CardModal";
import ConfirmModal from "./component/modal/ConfirmModal";
import Dropdown from "./component/Dropdown";
import SortableTable from "./component/SortableTable";

const iconImageSrc = (iconId: number): string => {
    const icon: string = iconId.toString().padStart(6, "0");
    return `https://xivapi.com/i/${icon.substr(0, 3)}000/${icon}.png`;
};

const toLocaleString = (value: number) => value.toLocaleString();

const toPercentString = (value: number, decimals: number = 2): string =>
    `${(value * 100).toFixed(decimals)}%`;

type WatchListProps = {
    onDeleteWatch: (arg0: Watch) => void,
    watches: undefined | Watch[],
};

const WatchListTable = ({onDeleteWatch, watches}: WatchListProps & { watches: Watch[] }) => {
    const [selectedWatch, setSelectedWatch] = useState<Watch>();
    const [materialModalActive, showMaterialModal, hideMaterialModal] = useModal();
    const [deleteModalActive, showDeleteModal, hideDeleteModal] = useModal();

    function renderDropdown(watch: Watch) {
        const items = [];
        if (watch.materials.length) {
            items.push({
                label: "Materials",
                onClick: () => {
                    setSelectedWatch(watch);
                    showMaterialModal();
                }
            });
            items.push(Dropdown.divider());
        }
        items.push({
            label: "Delete",
            onClick: () => {
                setSelectedWatch(watch);
                showDeleteModal();
            }
        });

        return <Dropdown
            items={items}
            label="Actions"
        />;
    }

    return <>
        {selectedWatch && <CardModal
            active={materialModalActive}
            close={hideMaterialModal}
            title={`Materials for ${selectedWatch.name}`}
        >
            <SortableTable
                className="is-bordered is-striped"
                columns={[
                    {header: "Name", key: "name"},
                    {header: "Unit Min", key: "minimum", transform: toLocaleString},
                    {header: "Unit Max", key: "maximum", transform: toLocaleString},
                    {header: "Quantity", key: "quantity"},
                ]}
                data={selectedWatch.materials}
                deriveKeys={material => {
                    const minimum = material.datacenterMinimum;
                    const maximum = Math.round(material.datacenterMean + material.datacenterDeviation);
                    return {minimum, maximum};
                }}
            />
        </CardModal>}
        {selectedWatch && <ConfirmModal
            active={deleteModalActive}
            close={hideDeleteModal}
            onYes={async () => {
                await deleteWatch(selectedWatch);
                onDeleteWatch(selectedWatch);
            }}
            title="Delete Watch"
        >
            <p>Stop watching {selectedWatch.name}?</p>
        </ConfirmModal>}
        <SortableTable<Watch, {
            min: number,
            max: number,
            materialsMin: number,
            materialsMax: number,
            profitMin: number,
            profitMax: number,
        }>
            className="is-striped"
            columns={[
                {className: "is-narrow", header: "", render: renderDropdown},
                {
                    header: "Item Name",
                    key: "name",
                    render: row => <>
                        <img
                            className="mr-1"
                            alt={`Icon for ${row.name}`}
                            src={iconImageSrc(row.icon)}
                            style={{height: "30px", verticalAlign: "middle"}}
                        />
                        {row.name}
                    </>
                },
                {header: "Item Min", key: "min", tooltip: "Lamia Price", transform: toLocaleString},
                {header: "Item Max", key: "max", tooltip: "Lamia Price", transform: toLocaleString},
                {header: "Materials Min", key: "materialsMin", tooltip: "Datacenter Price", transform: toLocaleString},
                {header: "Materials Max", key: "materialsMax", tooltip: "Datacenter Price", transform: toLocaleString},
                {
                    header: "Profit Min",
                    key: "profitMin",
                    render: watch => `${watch.profitMin.toLocaleString()} (${toPercentString(watch.profitMin / watch.max)})`
                },
                {
                    header: "Profit Max",
                    key: "profitMax",
                    render: watch => `${watch.profitMax.toLocaleString()} (${toPercentString(watch.profitMax / watch.min)})`
                },
            ]}
            data={watches}
            deriveKeys={watch => {
                const min = watch.worldMinimum;
                const max = Math.round(watch.worldMean + watch.worldDeviation);
                const materialsMin = watch.materials.reduce((sum, m) =>
                    sum + m.datacenterMinimum * m.quantity, 0);
                const materialsMax = Math.round(watch.materials.reduce((sum, m) =>
                    sum + (m.datacenterMean + m.datacenterDeviation) * m.quantity, 0));
                const profitMin = min - materialsMax;
                const profitMax = max - materialsMin;

                return {min, max, materialsMin, materialsMax, profitMin, profitMax};
            }}
        />
    </>;
}

const WatchList = ({onDeleteWatch, watches}: WatchListProps) => {
    let content;
    if (!watches) {
        content = <p className="has-text-centered">Loading...</p>;
    } else if (!watches.length) {
        content = <p className="has-text-centered">No watches added, why not add one from the item search?</p>;
    } else {
        content = <WatchListTable onDeleteWatch={onDeleteWatch} watches={watches}/>;
    }

    return <main className="content">{content}</main>;
}

const App = () => {
    const [loggedIn, logOut] = useLoginStatus();
    const [search, setSearch] = useState<string>("");
    const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
    const [watches, setWatches] = useState<Watch[]>();

    function onDeleteWatch(watch: Watch) {
        setWatches(watches && watches.filter(w => watch.id !== w.id));
    }

    function onSearchChange(e: React.ChangeEvent) {
        setSearch((e.target as HTMLInputElement).value);
    }

    useEffect(() => {
        function onDocumentClick() {
            setSearchResults([]);
        }

        (async () => {
            setWatches(await getWatches());
        })();

        document.addEventListener("click", onDocumentClick);
        return () => document.removeEventListener("click", onDocumentClick);
    }, []);

    useEffect(() => {
        if (!search.length) {
            setSearchResults([]);
            return;
        }

        const handle = setTimeout(async () => {
            setSearchResults(await getSearchResults(search));
        }, 500);

        return () => clearTimeout(handle);
    }, [search]);

    let navbarButtons, searchForm, content;
    if (loggedIn) {
        searchForm = <nav className="level mt-5">
            <div className="level-item">
                <form className="search" onSubmit={e => e.preventDefault()}>
                    <div className="field has-addons mb-0">
                        <div className="control">
                            <input
                                className="input"
                                onChange={onSearchChange}
                                placeholder="Search items/recipes..."
                                style={{width: "400px"}}
                                type="search"
                                value={search}
                            />
                        </div>
                        <div className="control">
                            <button className="button is-primary" type="submit">Search</button>
                        </div>
                    </div>
                    {searchResults.length > 0 && <div className="search-result">
                        {searchResults.map(result =>
                            <div
                                className="search-result-item"
                                key={`${result.type}:${result.id}`}
                                onClick={async () => setWatches(await createWatch(result.id, result.type))}
                                role="menuitem"
                            >
                                <div className="search-result-name">{result.name}</div>
                                <div className="search-result-type">{result.type}</div>
                            </div>
                        )}
                    </div>}
                </form>
            </div>
        </nav>;

        navbarButtons = <div className="buttons">
            <button className="button is-primary" onClick={logOut}>Log Out</button>
        </div>;

        content = <WatchList onDeleteWatch={onDeleteWatch} watches={watches}/>;
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
        {searchForm}
        <div className="container mt-6">{content}</div>
        <div className="footer mt-6">
            <div className="content has-text-centered">Copyright &copy; 2021 FFXIV Tools</div>
        </div>
    </>;
};

export default App;
