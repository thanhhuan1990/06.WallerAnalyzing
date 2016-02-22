package local.wallet.analyzing.View;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import local.wallet.analyzing.IPasscodeEnter;
import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 1/4/2016.
 */
public class PasscodeText implements View.OnClickListener  {
    private static final String TAG = "PasscodeText";

    private ImageView mPasscode1;
    private ImageView mPasscode2;
    private ImageView mPasscode3;
    private ImageView mPasscode4;

    private String inputtedContent = "";

    private String passcode = "";
    private Activity mActivity;
    private IPasscodeEnter mListener;

    private Button[] mDial = new Button[10];
    private Button     mDelete;
    private static final SparseArray<Character> Buttons; // ボタンの画像リソースIDとDTMFとして送信する文字を格納する.
    private FrameLayout mLayout;

    static {
        Buttons = new SparseArray<Character>();
        Buttons.append(R.id.btnPasscode1, '1');
        Buttons.append(R.id.btnPasscode2, '2');
        Buttons.append(R.id.btnPasscode3, '3');
        Buttons.append(R.id.btnPasscode4, '4');
        Buttons.append(R.id.btnPasscode5, '5');
        Buttons.append(R.id.btnPasscode6, '6');
        Buttons.append(R.id.btnPasscode7, '7');
        Buttons.append(R.id.btnPasscode8, '8');
        Buttons.append(R.id.btnPasscode9, '9');
        Buttons.append(R.id.btnPasscode0, '0');
    }

    public PasscodeText(Activity activity, IPasscodeEnter mListener, String passcode) {
        LogUtils.logEnterFunction(TAG, null);

        this.mListener  = mListener;
        mActivity = activity;
        this.passcode = passcode;

        inputtedContent = "";

        try {

            mLayout = (FrameLayout) mActivity.findViewById(R.id.layout_dial);

            mActivity.getLayoutInflater().inflate(R.layout.layout_dtmf, mLayout);

            mPasscode1  = (ImageView) mActivity.findViewById(R.id.passcode_1);
            mPasscode2  = (ImageView) mActivity.findViewById(R.id.passcode_2);
            mPasscode3  = (ImageView) mActivity.findViewById(R.id.passcode_3);
            mPasscode4  = (ImageView) mActivity.findViewById(R.id.passcode_4);

            for (int i = 0, size = Buttons.size(); i < size; i++) {
                mDial[i] = (Button) mActivity.findViewById(Buttons.keyAt(i));
                mDial[i].setOnClickListener(this);
            }

            mDelete = (Button) mActivity.findViewById(R.id.btnDelete);
            mDelete.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
            if (mLayout != null) {
                mLayout.removeAllViews();
            }
        }

        LogUtils.trace(TAG, "PasscodeText");
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onClick(View v) {
        try {
            if(v != mDelete) {
                char code = Buttons.get(v.getId());
                updatePasscode(code + "");
            } else {
                deletePasscode();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean updatePasscode(String content) {
        boolean result = false;

        inputtedContent += content;

        switch (inputtedContent.length()) {
            case 1:
                mPasscode1.setImageResource(R.drawable.img_number_background);
                break;
            case 2:
                mPasscode2.setImageResource(R.drawable.img_number_background);
                break;
            case 3:
                mPasscode3.setImageResource(R.drawable.img_number_background);
                break;
            case 4:
                mPasscode4.setImageResource(R.drawable.img_number_background);
                // Notify login result
                mListener.onPasscodeResult(inputtedContent.equals(passcode));
                break;
            default:
                break;
        }

        return result;
    }

    private void deletePasscode() {
        inputtedContent = inputtedContent.substring(0, inputtedContent.length()-1);
        switch (inputtedContent.length()) {
            case 0:
                mPasscode1.setImageResource(R.drawable.img_passcode_null);
                break;
            case 1:
                mPasscode2.setImageResource(R.drawable.img_passcode_null);
                break;
            case 2:
                mPasscode3.setImageResource(R.drawable.img_passcode_null);
                break;
            case 3:
                mPasscode4.setImageResource(R.drawable.img_passcode_null);
            default:
                break;
        }
    }

    public void clearPasscode() {
        inputtedContent = "";
        mPasscode1.setImageResource(R.drawable.img_passcode_null);
        mPasscode2.setImageResource(R.drawable.img_passcode_null);
        mPasscode3.setImageResource(R.drawable.img_passcode_null);
        mPasscode4.setImageResource(R.drawable.img_passcode_null);
    }
}
