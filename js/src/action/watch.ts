export async function createWatch(id: number, type: string): Promise<Watch[]> {
    const response = await fetch("/api/v1/watches", {
        method: "POST",
        headers: {
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
    });
}

export async function getWatches(): Promise<Watch[]> {
    const response = await fetch("/api/v1/watches");
    return await response.json();
}
