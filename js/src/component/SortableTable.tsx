import React, {useMemo, useState} from "react";

type Settings = {
    dir: number,
    key?: string,
};

type HeadingProps = {
    className?: string,
    "data-tooltip"?: string,
    onClick?: () => void,
}

export type SortableTableProps<T, D> = {
    className?: string,
    columns: {
        className?: string,
        header: string,
        key?: string,
        render?: (arg0: T & D) => string | JSX.Element,
        sortable?: boolean,
        tooltip?: string,
        transform?: (arg0: any) => any,
    }[],
    data: T[],
    deriveKeys?: (arg0: T) => D,
};

export default function SortableTable<T, D>(props: SortableTableProps<T, D>) {
    const {className, columns, data, deriveKeys} = props;
    const [settings, setSettings] = useState<Settings>({dir: 1});

    const derivedData = useMemo(() => data.map(row => {
        return deriveKeys ? {...row, ...deriveKeys(row)} : {...row} as T & D;
    }), [data, deriveKeys]);

    const sortedData = useMemo(() => {
        const dataCopy = [...derivedData];
        if (settings.key) {
            const {dir, key} = settings;
            dataCopy.sort((a: any, b: any) => {
                if (a[key] < b[key])
                    return -dir;
                if (a[key] > b[key])
                    return dir;
                return 0;
            });
        }
        return dataCopy;
    }, [derivedData, settings]);

    function onColumnClick(key: string) {
        let dir = 1;
        if (settings.key === key && settings.dir === 1) {
            dir = -1;
        }
        setSettings({dir, key});
    }

    return <table className={`table ${className || ""}`}>
        <thead>
        <tr>
            {columns.map(({className, header, key, sortable = true, tooltip}, index) => {
                const props: HeadingProps = {className};
                const isSortable = key && sortable;

                if (isSortable) {
                    props.className += " is-sortable";
                    props.onClick = () => onColumnClick(key);
                }
                if (tooltip) {
                    props["data-tooltip"] = tooltip;
                }

                let icon;
                if (isSortable) {
                    icon = <i className="fas fa-sort"/>;
                    if (key === settings.key) {
                        icon = settings.dir === 1
                            ? <i className="fas fa-sort-up"/>
                            : <i className="fas fa-sort-down"/>;
                    }
                }

                return <th className={className} key={index} {...props}>{header} {icon}</th>;
            })}
        </tr>
        </thead>
        <tbody>
        {sortedData.map((row: any, rowIndex) =>
            <tr key={rowIndex}>
                {columns.map((column, columnIndex) => {
                    let value;
                    if (column.render) {
                        value = column.render(row);
                    } else if (column.key) {
                        value = row[column.key];
                        if (column.transform) {
                            value = column.transform(value);
                        }
                    }
                    return <td key={columnIndex}>{value}</td>;
                })}
            </tr>
        )}
        </tbody>
    </table>;
}
