import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollId } = params;
    const res = await fetch(`/sploinkyboinkend/polls/${pollId}`);
    const data: Poll = await res.json();
    console.log('Fetched data:', data);

    if (data) {
        return { data };
    } else {
        return {
            status: 404,
            error: new Error('Poll not found'),
        };
    }
};
