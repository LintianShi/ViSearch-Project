package validation;

public class SearchConfiguration {
    private int searchMode; // 0: dfs, 1: bfs, 2: h*
    private int searchLimit;
    private int stateLimit;

    public SearchConfiguration() {
        this.searchMode = 0;
        this.searchLimit = -1;
        this.stateLimit = -1;
    }

    public SearchConfiguration(int searchMode, int searchLimit, int stateLimit) {
        if (searchMode >= 0 && searchMode <= 2) {
            this.searchMode = searchMode;
        } else {
            this.searchMode = 0;
        }
        this.searchLimit = searchLimit;
        this.stateLimit = stateLimit;
    }

    public int getSearchMode() {
        return searchMode;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public int getStateLimit() {
        return stateLimit;
    }
}
