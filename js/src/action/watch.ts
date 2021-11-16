import {authToken} from "../util/cookie";

function defaultHeaders(): HeadersInit {
    return {
        Authorization: `Bearer ${authToken()}`,
    };
}

export async function createWatch(id: number, type: string): Promise<Watch[]> {
    const response = await fetch("/api/v1/watches", {
        method: "POST",
        headers: {
            ...defaultHeaders(),
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            id,
            type: type.toUpperCase(),
        }),
    });

    return await response.json();
}

export async function deleteWatch(watch: Watch) {
    await fetch(`/api/v1/watches/${watch.id}`, {
        method: "DELETE",
        headers: defaultHeaders(),
    });
}

export async function getWatches(): Promise<Watch[]> {
    const response = await fetch("/api/v1/watches", {
        headers: defaultHeaders(),
    });

    return await response.json();
}
