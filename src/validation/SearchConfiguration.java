package validation;

public class SearchConfiguration {
    private int searchMode; // 0: dfs, 1: bfs, 2: h*

    public SearchConfiguration() {
        this.searchMode = 0;
    }

    public SearchConfiguration(int searchMode) {
        if (searchMode >= 0 && searchMode <= 2) {
            this.searchMode = searchMode;
        } else {
            this.searchMode = 0;
        }
    }

    public int getSearchMode() {
        return searchMode;
    }
}
