/// <reference types="react-scripts" />

type Material = {
    itemId: number,
    name: string,
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
    datacenterMinimum: number,
    datacenterMean: number,
    datacenterDeviation: number,
    worldMinimum: number,
    worldMean: number,
    worldDeviation: number,
    materials: Material[],
};
