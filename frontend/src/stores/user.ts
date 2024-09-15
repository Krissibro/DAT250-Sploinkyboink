import { writable } from 'svelte/store';
import type { User } from '../types';

function createUserStore() {
    let initialUser = null;

    if (typeof localStorage !== 'undefined') {
        const storedUser = localStorage.getItem('currentUser');
        initialUser = storedUser ? JSON.parse(storedUser) : null;
    }

    const { subscribe, set } = writable<User | null>(initialUser);

    return {
        subscribe,
        set: (user: User | null) => {
            if (typeof localStorage !== 'undefined') {
                localStorage.setItem('currentUser', JSON.stringify(user));
            }
            set(user);
        },
        reset: () => {
            if (typeof localStorage !== 'undefined') {
                localStorage.removeItem('currentUser');
            }
            set(null);
        },
    };
}

export const currentUser = createUserStore();
