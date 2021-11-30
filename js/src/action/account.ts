type UpdateCurrentAccountBody = {
    name: string,
    datacenterId: number,
    worldId: number,
};

export async function getCurrentAccount(): Promise<Account> {
    const response = await fetch("/api/v1/accounts/@me");
    return await response.json();
}

export async function updateCurrentAccount(body: UpdateCurrentAccountBody): Promise<Account> {
    const response = await fetch("/api/v1/accounts/@me", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    });

    return await response.json();
}
