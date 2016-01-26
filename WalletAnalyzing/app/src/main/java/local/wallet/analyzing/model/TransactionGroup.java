package local.wallet.analyzing.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by huynh.thanh.huan on 1/26/2016.
 */
public class TransactionGroup {

    private Calendar time;
    private List<Transaction> arTrans = new ArrayList<Transaction>();

    public TransactionGroup() {}

    private void addTransaction(Transaction trans) {
        arTrans.add(trans);
    }

    public static List<TransactionGroup> parseTransactions(List<Transaction> arTrans) {
        List<TransactionGroup> group = new ArrayList<TransactionGroup>();

        Collections.sort(arTrans);

        for(Transaction tran : arTrans) {
            if(group.size() == 0 || tran.getTime().get(Calendar.DAY_OF_YEAR) != group.get(group.size() - 1).time.get(Calendar.DAY_OF_YEAR)) {
                TransactionGroup date = new TransactionGroup();
                date.time = tran.getTime();
                date.addTransaction(tran);
                group.add(date);
            } else if(tran.getTime().get(Calendar.DAY_OF_YEAR) == group.get(group.size() - 1).time.get(Calendar.DAY_OF_YEAR)) {
                group.get(group.size() - 1).addTransaction(tran);
            }
        }

        return group;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public List<Transaction> getArTrans() {
        return arTrans;
    }

    public void setArTrans(List<Transaction> arTrans) {
        this.arTrans = arTrans;
    }
}
