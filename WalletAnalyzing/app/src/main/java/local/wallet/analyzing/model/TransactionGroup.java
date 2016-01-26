package local.wallet.analyzing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by huynh.thanh.huan on 1/26/2016.
 */
public class TransactionGroup {

    private Date date;
    private List<Transaction> arTrans = new ArrayList<Transaction>();

    public TransactionGroup() {}

    private void addTransaction(Transaction trans) {
        arTrans.add(trans);
    }

    public static List<TransactionGroup> parseTransactions(List<Transaction> arTrans) {
        List<TransactionGroup> group = new ArrayList<TransactionGroup>();

        Collections.sort(arTrans);

        for(Transaction tran : arTrans) {
            if(group.size() == 0 || tran.getTime().getDate() != group.get(group.size() - 1).date.getDate()) {
                TransactionGroup date = new TransactionGroup();
                date.date = tran.getTime();
                date.addTransaction(tran);
                group.add(date);
            } else if(tran.getTime().getDate() == group.get(group.size() - 1).date.getDate()) {
                group.get(group.size() - 1).addTransaction(tran);
            }
        }

        return group;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Transaction> getArTrans() {
        return arTrans;
    }

    public void setArTrans(List<Transaction> arTrans) {
        this.arTrans = arTrans;
    }
}
