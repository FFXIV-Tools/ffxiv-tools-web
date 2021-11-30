/// <reference types="react-scripts" />

type Account = {
    id: Long,
    name: string,
    datacenterId: Int,
    worldId: Int,
};

type Assignable = {
    [key: string]: any,
};

type Material = {
    itemId: number,
    name: string,
    icon: number,
    quantity: number,
    datacenterMinimum: number,
    datacenterMean: number,
    datacenterDeviation: number,
    worldMinimum: number,
    worldMean: number,
    worldDeviation: number,
};

type SearchResult = {
    id: number,
    type: string,
    icon: number,
    name: string,
};

type Watch = {
    id: number,
    itemId: number,
    name: string,
    icon: number,
    datacenterMinimum: number,
    datacenterMean: number,
    datacenterDeviation: number,
    worldMinimum: number,
    worldMean: number,
    worldDeviation: number,
    materials: Material[],
};
