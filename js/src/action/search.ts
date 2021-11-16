import {authToken} from "../util/cookie";

export async function getSearchResults(query: string): Promise<SearchResult[]> {
    const encodedQuery = encodeURIComponent(query);
    const response = await fetch(`/api/v1/search?query=${encodedQuery}`, {
        headers: {
            Authorization: `Bearer ${authToken()}`,
        },
    });

    return await response.json();
}
