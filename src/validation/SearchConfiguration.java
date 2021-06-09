package validation;

import datatype.AbstractDataType;

public class SearchConfiguration implements Cloneable {
    private int searchMode; // 0: dfs, 1: bfs, 2: h*
    private int searchLimit;
    private int queueLimit;
    private int visibilityLimit;
    private boolean findAllAbstractExecution;
    private AbstractDataType adt;

    public SearchConfiguration() {
        this.searchMode = 0;
        this.searchLimit = -1;
        this.queueLimit = -1;
    }

    public SearchConfiguration(int searchMode, int searchLimit, int queueLimit, int visibilityLimit, boolean findAllAbstractExecution) {
        if (searchMode >= 0 && searchMode <= 2) {
            this.searchMode = searchMode;
        } else {
            this.searchMode = 0;
        }
        this.searchLimit = searchLimit;
        this.queueLimit = queueLimit;
        this.visibilityLimit = visibilityLimit;
        this.findAllAbstractExecution = findAllAbstractExecution;
    }

    public int getSearchMode() {
        return searchMode;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public int getQueueLimit() {
        return queueLimit;
    }

    public int getVisibilityLimit() {
        return visibilityLimit;
    }

    public void setAdt(AbstractDataType adt) {
        this.adt = adt;
    }

    public AbstractDataType getAdt() {
        return adt;
    }

    public boolean isFindAllAbstractExecution() {
        return findAllAbstractExecution;
    }

    @Override
    protected Object clone() {
        SearchConfiguration configuration =
                new SearchConfiguration(this.searchMode, this.searchLimit, this.queueLimit, this.visibilityLimit, this.findAllAbstractExecution);
        configuration.setAdt(adt.createInstance());
        return configuration;
    }
}
