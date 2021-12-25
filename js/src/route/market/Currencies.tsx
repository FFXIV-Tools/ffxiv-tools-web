import React, {useEffect, useState} from "react";

import SortableTable from "../../component/SortableTable";

import {iconImageSrc} from "../../util/xivapi";

const renderIconImg = (iconId?: number, name?: string, size = 20) => (
    <img
        className="mr-1"
        alt={`Icon for ${name}`}
        src={iconImageSrc(iconId || 0)}
        style={{height: `${size}px`, verticalAlign: "middle"}}
    />
);

const toLocaleString = (value: number) => value.toLocaleString();

const Currencies = () => {
    const [currencies, setCurrencies] = useState<Currency[]>();
    const [currency, setCurrency] = useState<Currency>();
    const [items, setItems] = useState<CurrencyItem[]>();

    useEffect(() => {
        (async () => {
            const response = await fetch("/api/v1/market/currencies");
            const currencies = await response.json();
            setCurrencies(currencies);
            setCurrency(currencies[0]);
        })();
    }, []);

    useEffect(() => {
        (async () => {
            if (!currency) {
                setItems(undefined);
                return;
            }

            const response = await fetch(`/api/v1/market/currencies/${currency.id}`);
            setItems(await response.json());
        })();
    }, [currency]);

    if (!currencies) {
        return <p className="has-text-centered">Loading...</p>;
    }

    return <>
        <div className="tabs is-boxed is-centered is-small">
            <ul>
                {currencies.map(c =>
                    <li key={c.id} className={c.id === currency?.id ? "is-active" : ""}>
                        <a onClick={() => setCurrency(c)}>
                            {renderIconImg(c.icon, c.name)}
                            {c.name}
                        </a>
                    </li>
                )}
            </ul>
        </div>
        {items && <div className="content">
            <SortableTable
                className="is-bordered is-striped"
                columns={[
                    {
                        header: "Item Name",
                        key: "name",
                        render: row => <a
                            href={`https://universalis.app/market/${row.id}`}
                            rel="noreferrer"
                            target="_blank"
                        >
                            {renderIconImg(row.icon, row.name)}
                            {row.name}
                        </a>
                    },
                    {
                        header: "Unit Min",
                        key: "worldMinimum",
                        transform: toLocaleString,
                    },
                    {
                        header: "Unit Max",
                        key: "worldMax",
                        transform: toLocaleString,
                    },
                    {
                        header: "Gil/$ Min",
                        key: "gilMin",
                        transform: toLocaleString,
                    },
                    {
                        header: "Gil/$ Max",
                        key: "gilMax",
                        transform: toLocaleString,
                    },
                ]}
                data={items}
                deriveKeys={item => {
                    const worldMax = Math.round(item.worldMean + item.worldDeviation);
                    return {
                        gilMin: Math.round(item.worldMinimum / item.cost * item.quantity),
                        gilMax: Math.round(worldMax / item.cost * item.quantity),
                        worldMax,
                    };
                }}
            />
        </div>}
    </>;
};

export default Currencies;
