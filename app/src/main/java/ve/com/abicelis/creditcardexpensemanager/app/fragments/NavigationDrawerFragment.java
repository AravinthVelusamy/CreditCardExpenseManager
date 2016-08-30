package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.NavigationDrawerAdapter;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener{

    public static final String ACTIVE_CREDIT_CARD = "active_cc";

    //DATA
    private CreditCard mActiveCreditCard = null;

    //UI
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    TextView bankName;
    TextView cardNumber;
    TextView cardExpiration;
    ImageView cardType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container);

        View creditCardView = view.findViewById(R.id.nav_drawer_credit_card);
        bankName = (TextView) creditCardView.findViewById(R.id.list_item_credit_card_bank_name);
        cardNumber = (TextView) creditCardView.findViewById(R.id.list_item_credit_card_number);
        cardExpiration = (TextView) creditCardView.findViewById(R.id.list_item_credit_card_expiration);
        cardType = (ImageView) creditCardView.findViewById(R.id.list_item_credit_card_type);

        //Bundle bundle = getArguments();
        //mActiveCreditCard = (CreditCard) bundle.getSerializable(ACTIVE_CREDIT_CARD);

        setUpRecyclerView(view);
        //setUpActiveCC(view);

        return view;
    }

    private void setUpActiveCC(CreditCard activeCreditCard) {
        mActiveCreditCard = activeCreditCard;

        bankName.setText(mActiveCreditCard.getBankName());
        cardNumber.setText(mActiveCreditCard.getCardNumber());
        cardExpiration.setText(mActiveCreditCard.getShortCardExpirationString());

        switch(mActiveCreditCard.getCardType()) {
            case AMEX:
                cardType.setImageResource(R.drawable.logo_amex);
                break;
            case DISCOVER:
                cardType.setImageResource(R.drawable.logo_discover);
                break;
            case MASTERCARD:
                cardType.setImageResource(R.drawable.logo_mastercard);
                break;
            case VISA:
                cardType.setImageResource(R.drawable.logo_visa);
                break;
        }
    }

    private void setUpRecyclerView(View view) {

        List<NavigationDrawerItem> data = new ArrayList<>();
        data.add(new NavigationDrawerItem("Home", android.R.drawable.ic_dialog_alert));
        data.add(new NavigationDrawerItem("Map", android.R.drawable.ic_dialog_map));
        data.add(new NavigationDrawerItem("Info", android.R.drawable.ic_dialog_info));
        data.add(new NavigationDrawerItem("Dialer", android.R.drawable.ic_dialog_dialer));
        data.add(new NavigationDrawerItem("Email", android.R.drawable.ic_dialog_email));


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.nad_drawer_recyclerview_list);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, CreditCard activeCreditCard) {

        setUpActiveCC(activeCreditCard);

        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();

            }
        });
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(), "NAvDrawer Something was clicked", Toast.LENGTH_SHORT).show();
    }
}
