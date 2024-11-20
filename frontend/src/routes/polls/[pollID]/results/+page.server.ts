import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollID } = params;
    const res = await fetch(`/sploinkyboinkend/polls/${pollID}/results`);
    if (res.ok) {
        const results = await res.json();
        return { results };
    } else {
        return {
            status: res.status,
            error: new Error('Results not found'),
        };
    }
};