package ve.com.abicelis.creditcardexpensemanager.enums;

/**
 * Created by Alex on 6/8/2016.
 */
public enum ExpenseCategory {

    FOOD("Food"),
    LEISURE("Leisure"),
    ENTERTAINMENT("Entertainment"),
    CLOTHING("Clothing"),
    EDUCATION("Education");



    private String mFriendlyName;

    ExpenseCategory(String friendlyName) {
        mFriendlyName = friendlyName;
    }


    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    @Override
    public String toString() {
        return mFriendlyName;
    }


}
