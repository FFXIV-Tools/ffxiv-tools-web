import React, {useEffect, useState} from "react";

import {createWatch, deleteWatch, getWatches} from "../action/watch";
import {getSearchResults} from "../action/search";

import CardModal from "../component/modal/CardModal";
import ConfirmModal from "../component/modal/ConfirmModal";
import Dropdown, {DropdownItem} from "../component/Dropdown";
import Icon from "../component/Icon";
import SortableTable from "../component/SortableTable";

import {useModal} from "../hook/modal";

import {iconImageSrc} from "../util/xivapi";

const GST = 1.05;

const toLocaleString = (value: number) => value.toLocaleString();

const toPercentString = (value: number, decimals = 1) => `${(value * 100).toFixed(decimals)}%`;

type WatchListProps = {
    onDeleteWatch: (arg0: Watch) => void,
    watches: undefined | Watch[],
};

const WatchListTable = ({onDeleteWatch, watches}: WatchListProps & { watches: Watch[] }) => {
    const [selectedWatch, setSelectedWatch] = useState<Watch>();
    const [materialModalActive, showMaterialModal, hideMaterialModal] = useModal();
    const [deleteModalActive, showDeleteModal, hideDeleteModal] = useModal();

    return <>
        {selectedWatch && <CardModal
            active={materialModalActive}
            close={hideMaterialModal}
            title={`Materials for ${selectedWatch.name}`}
        >
            <SortableTable
                className="is-bordered is-striped"
                columns={[
                    {
                        header: "Name",
                        key: "name",
                        render: row => <a
                            href={`https://universalis.app/market/${row.itemId}`}
                            rel="noreferrer"
                            target="_blank"
                        >
                            <img
                                className="mr-1"
                                alt={`Icon for ${row.name}`}
                                src={iconImageSrc(row.icon)}
                                style={{height: "30px", verticalAlign: "middle"}}
                            />
                            {row.name}
                        </a>,
                    },
                    {header: "Unit Min", key: "minimum", transform: toLocaleString},
                    {header: "Unit Max", key: "maximum", transform: toLocaleString},
                    {header: "Quantity", key: "quantity", sortable: false},
                ]}
                data={selectedWatch.materials}
                deriveKeys={material => {
                    const minimum = Math.round(material.datacenterMinimum * GST);
                    const maximum = Math.round(material.datacenterMean + material.datacenterDeviation * GST);
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
                {
                    header: "Item",
                    key: "name",
                    render: row => <a
                        href={`https://universalis.app/market/${row.itemId}`}
                        rel="noreferrer"
                        target="_blank"
                    >
                        <img
                            className="mr-1"
                            alt={`Icon for ${row.name}`}
                            src={iconImageSrc(row.icon)}
                            style={{height: "30px", verticalAlign: "middle"}}
                        />
                        {row.name}
                    </a>,
                },
                {header: "Unit Min", key: "min", tooltip: "Lamia Price", transform: toLocaleString},
                {header: "Unit Max", key: "max", tooltip: "Lamia Price", transform: toLocaleString},
                {header: "Materials Min", key: "materialsMin", tooltip: "Datacenter Price", transform: toLocaleString},
                {header: "Materials Max", key: "materialsMax", tooltip: "Datacenter Price", transform: toLocaleString},
                {
                    header: "Profit Min",
                    key: "profitMin",
                    render: w => `${w.profitMin.toLocaleString()} (${toPercentString(w.profitMin / w.min)})`
                },
                {
                    header: "Profit Max",
                    key: "profitMax",
                    render: w => `${w.profitMax.toLocaleString()} (${toPercentString(w.profitMax / w.max)})`
                },
                {
                    className: "is-narrow",
                    header: "",
                    render: watch => {
                        let items: DropdownItem[] = [
                            {
                                icon: "trash",
                                label: "Delete",
                                onClick: () => {
                                    setSelectedWatch(watch);
                                    showDeleteModal();
                                },
                            },
                        ];

                        if (watch.materials.length) {
                            items = [
                                {
                                    icon: "shopping-cart",
                                    label: "Materials",
                                    onClick: () => {
                                        setSelectedWatch(watch);
                                        showMaterialModal();
                                    },
                                },
                                Dropdown.divider(),
                                ...items,
                            ];
                        }

                        return <Dropdown
                            buttonClassName="is-small is-rounded"
                            className="is-right"
                            icon="bars"
                            items={items}
                        />;
                    }
                },
            ]}
            data={watches}
            deriveKeys={watch => {
                const min = watch.worldMinimum;
                const max = Math.round(watch.worldMean + watch.worldDeviation);
                const materialsMin = Math.round(watch.materials.reduce((sum, m) =>
                    sum + m.datacenterMinimum * m.quantity, 0) * GST);
                const materialsMax = Math.round(watch.materials.reduce((sum, m) =>
                    sum + (m.datacenterMean + m.datacenterDeviation) * m.quantity, 0) * GST);
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

const Watches = () => {
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

    return <>
        <nav className="level mt-5">
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
                            <button className="button is-primary" type="submit">
                                <Icon name="search"/>
                                <span>Search</span>
                            </button>
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
                                <div className="search-result-name">
                                    <img
                                        src={iconImageSrc(result.icon)}
                                        alt={`Icon for ${result.name}`}
                                    />
                                    {result.name}
                                </div>
                                <div className="search-result-type">{result.type}</div>
                            </div>
                        )}
                    </div>}
                </form>
            </div>
        </nav>
        <WatchList onDeleteWatch={onDeleteWatch} watches={watches}/>
    </>;
};

export default Watches;
