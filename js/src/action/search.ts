export async function getSearchResults(query: string): Promise<SearchResult[]> {
    const encodedQuery = encodeURIComponent(query);
    const response = await fetch(`/api/v1/search?query=${encodedQuery}`);

    return await response.json();
}
