package validation;

import arbitration.VisibilityType;
import datatype.AbstractDataType;

public class SearchConfiguration implements Cloneable {
    private int searchMode; // 0: dfs, 1: bfs, 2: h*
    private int searchLimit;
    private int queueLimit;
    private int visibilityLimit;
    private boolean findAllAbstractExecution = false;
    private boolean enablePrickOperation = true;
    private VisibilityType visibilityType = VisibilityType.CAUSAL;
    private AbstractDataType adt;

    public SearchConfiguration() {
        this.searchMode = 0;
        this.searchLimit = -1;
        this.queueLimit = -1;
    }

    public SearchConfiguration(int searchMode, int searchLimit, int queueLimit, int visibilityLimit) {
        if (searchMode >= 0 && searchMode <= 2) {
            this.searchMode = searchMode;
        } else {
            this.searchMode = 0;
        }
        this.searchLimit = searchLimit;
        this.queueLimit = queueLimit;
        this.visibilityLimit = visibilityLimit;
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

    public void setFindAllAbstractExecution(boolean findAllAbstractExecution) {
        this.findAllAbstractExecution = findAllAbstractExecution;
    }

    public boolean isEnablePrickOperation() {
        return enablePrickOperation;
    }

    public void setEnablePrickOperation(boolean enablePrickOperation) {
        this.enablePrickOperation = enablePrickOperation;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    @Override
    protected Object clone() {
        SearchConfiguration configuration =
                new SearchConfiguration(this.searchMode, this.searchLimit, this.queueLimit, this.visibilityLimit);
        configuration.setAdt(adt.createInstance());
        configuration.setEnablePrickOperation(this.enablePrickOperation);
        configuration.setFindAllAbstractExecution(this.findAllAbstractExecution);
        configuration.setVisibilityType(this.visibilityType);
        return configuration;
    }
}
