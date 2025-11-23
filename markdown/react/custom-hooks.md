# Custom Hook 
This is just a document of my pratice with react custom hook. The project is too small for so I did not add any code links for screen shot, just what I have learned.

## What Changed

I extracted the **data fetching logic** into a reusable custom hook called `useFetch`, eliminating code duplication across components that fetch data from the backend.

## Why It Matters

This change demonstrates a core React hooks pattern: **abstracting repetitive stateful logic into custom hooks**. Before this refactor, both App.jsx and AvailablePlaces.jsx had nearly identical `useState` + `useEffect` combinations for managing loading/error/data states. Now they share the same logic through `useFetch`.

---

## Changes Made

### 1. Created useFetch.js (New File)

```javascript
// NEW CUSTOM HOOK
export function useFetch(fetchFn, initialValue) {
  const [isLoading, setIsLoading] = useState();
  const [error, setError] = useState();
  const [fetchedData, setFetchedData] = useState(initialValue);
  
  useEffect(() => {
    async function fetchData() {
      setIsLoading(true);
      try {
        const data = await fetchFn();
        setFetchedData(data);
      } catch (error) {
        setError({ message: error.message || "Failed to fetch data." });
      }
      setIsLoading(false);
    }
    fetchData();
  }, [fetchFn]);

  return { isLoading, error, fetchedData, setFetchedData, setError };
}
```

**Key features:**
- Accepts any `fetchFn` (function that returns a promise)
- Returns both data and setter functions (`setFetchedData`, `setError`) for external updates
- Encapsulates the loading/error/data triple-state pattern

---

### 2. Refactored App.jsx

**Before:**
```javascript
const [isFetching, setIsFetching] = useState(false);
const [error, setError] = useState();
const [userPlaces, setUserPlaces] = useState([]);

useEffect(() => {
  async function fetchPlaces() {
    setIsFetching(true);
    try {
      const places = await fetchUserPlaces();
      setUserPlaces(places);
    } catch (error) {
      setError({ message: error.message || 'Failed to fetch user places.' });
    }
    setIsFetching(false);
  }
  fetchPlaces();
}, []);
```

**After:**
```javascript
const { 
  isLoading: isFetching, 
  error, 
  fetchedData: userPlaces,
  setFetchedData: setUserPlaces 
} = useFetch(fetchUserPlaces, []);
```

**Result:** 15+ lines → 1 line. The hook handles all fetch orchestration.

---

### 3. Refactored AvailablePlaces.jsx

**Before:**
```javascript
const [isFetching, setIsFetching] = useState(false);
const [error, setError] = useState();
const [availablePlaces, setAvailablePlaces] = useState([]);

useEffect(() => {
  async function fetchPlaces() {
    setIsFetching(true);
    try {
      const places = await fetchAvailablePlaces();
      // ...geolocation sorting logic
      setAvailablePlaces(sortedPlaces);
    } catch (error) {
      setError({ message: error.message || 'Failed to fetch places.' });
    }
    setIsFetching(false);
  }
  fetchPlaces();
}, []);
```

**After:**
```javascript
// Created wrapper function for geolocation + sorting
async function fetchSortedPlaces() {
  const places = await fetchAvailablePlaces();
  return new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const sortedPlaces = sortPlacesByDistance(
          places,
          position.coords.latitude,
          position.coords.longitude
        );
        resolve(sortedPlaces);
      },
      (error) => reject(error)
    );
  });
}

const { 
  isLoading: isFetching, 
  error, 
  fetchedData: availablePlaces 
} = useFetch(fetchSortedPlaces, []);
```

**Result:** The geolocation logic moved into `fetchSortedPlaces`, which `useFetch` treats as any async function.

---

## Why These Changes Align With React Hooks Patterns

1. **DRY Principle**: Eliminated duplicate fetch/loading/error handling code
2. **Composition**: `useFetch` is generic enough to handle both simple fetches (`fetchUserPlaces`) and complex ones (`fetchSortedPlaces` with geolocation)
3. **Separation of Concerns**: Components focus on UI logic; `useFetch` handles async state management
4. **Hooks Rules Compliance**: Custom hook follows naming convention (`use` prefix) and uses built-in hooks correctly

This refactor transforms the codebase from showing individual hook usage to demonstrating **custom hook creation** - the next level of React hooks mastery.