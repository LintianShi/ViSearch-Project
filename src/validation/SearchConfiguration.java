package validation;

import datatype.AbstractDataType;

public class SearchConfiguration implements Cloneable {
    private int searchMode; // 0: dfs, 1: bfs, 2: h*
    private int searchLimit;
    private int stateLimit;
    private AbstractDataType adt;

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

    public void setAdt(AbstractDataType adt) {
        this.adt = adt;
    }

    public AbstractDataType getAdt() {
        return adt;
    }

    @Override
    protected Object clone() {
        SearchConfiguration configuration = new SearchConfiguration(this.searchMode, this.searchLimit, this.stateLimit);
        configuration.setAdt(adt.createInstance());
        return configuration;
    }
}
