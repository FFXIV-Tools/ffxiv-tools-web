/// <reference types="react-scripts" />

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
