import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollID } = params;
    const res = await fetch(`/sploinkyboinkend/polls/${pollID}`);
    const data = await res.json();

    if (data) {
        return { data };
    } else {
        return {
            status: 404,
            error: new Error('Poll not found'),
        };
    }
};
