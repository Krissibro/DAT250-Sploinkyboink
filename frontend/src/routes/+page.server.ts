// src/routes/+page.server.ts

import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ url, fetch }) => {
    // Extract 'page' from query parameters; default to 0 if not provided
    const pageParam = parseInt(url.searchParams.get('page') || '0', 10);
    const sizeParam = 12; // You can make this dynamic if needed

    // Fetch paginated polls from the backend
    const res = await fetch(`/sploinkyboinkend/polls/active?page=${pageParam}&size=${sizeParam}`);

    if (!res.ok) {
        console.error('Failed to fetch polls:', res.status, res.statusText);
        return { polls: [], pagination: { totalPages: 0, totalElements: 0, page: 0, size: sizeParam, numberOfElements: 0, first: true, last: true } };
    }

    const data = await res.json();

    const polls: Poll[] = data.content;
    const pagination = {
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        page: data.number, // Current page number (zero-based)
        size: data.size,
        numberOfElements: data.numberOfElements,
        first: data.first,
        last: data.last,
    };

    return { polls, pagination };
};
