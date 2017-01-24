package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.AddCreditCardActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.WelcomeActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateOrEditExpenseDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.ExpensesViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 3/9/2016.
 */
public class CreditCardListFragment extends Fragment {

    //Data
    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    List<Expense> creditCardExpenses = new ArrayList<>();
    ExpenseManagerDAO dao;

    //UI
    RecyclerView recycler;
    LinearLayoutManager layoutManager;
    ExpensesAdapter adapter;
    FloatingActionButton fabNewCreditCard;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_credit_cards));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            try {
                refreshData();
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), "Sorry, there was a problem loading the Credit Card", Toast.LENGTH_SHORT).show();
            }catch (CreditPeriodNotFoundException e) {
                Toast.makeText(getActivity(), "Sorry, there was a problem loading the Credit Period", Toast.LENGTH_SHORT).show();
            }
        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
            Toast.makeText(getActivity(), "Megapeo en oncreate, SharedPreferenceNotFoundException CreditCardNotFoundException", Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credit_card_list, container, false);

        setUpRecyclerView(rootView);
        setUpSwipeRefresh(rootView);
        setUpFab(rootView);

        return rootView;
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        //If coming back from expenseDetailActivity and data was edited/deleted, refresh.
//        if (requestCode == Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE && resultCode == Constants.RESULT_REFRESH_DATA) {
//            Toast.makeText(getActivity(), "Refreshing Expenses!", Toast.LENGTH_SHORT).show();
//            refreshRecyclerView();
//        }
//
//    }


    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void setUpRecyclerView(View rootView) {

        recycler = (RecyclerView) rootView.findViewById(R.id.ccl_recycler);

        ExpensesViewHolder.ExpenseDeletedListener listener = new ExpensesViewHolder.ExpenseDeletedListener() {
            @Override
            public void OnExpenseDeleted(int position) {
//                try {
//                    dao.deleteExpense(creditCardExpenses.get(position).getId());
//                    creditCardExpenses.remove(position);
//                    adapter.notifyItemRemoved(position);
//                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
//                    //refreshChart();
//                }catch (CouldNotDeleteDataException e) {
//                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
//               }

            }
        };

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), layoutManager.getOrientation()));
        recycler.setLayoutManager(layoutManager);

        adapter = new ExpensesAdapter(this, creditCardExpenses, activeCreditCard.getCreditPeriods().get(0).getId(), listener);
        recycler.setAdapter(adapter);

    }

    private void setUpSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ccl_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        //refreshChart();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }

//    private void setUpToolbar(View rootView) {
//
//        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.home_collapsing);
//        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.home_appbar);
//        toolbar = (Toolbar) rootView.findViewById(R.id.home_toolbar);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
//                    isShow = true;
//                } else if(isShow) {
//                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
//
//                    isShow = false;
//                }
//            }
//        });
//    }

    private void setUpFab(View rootView) {
        fabNewCreditCard = (FloatingActionButton) rootView.findViewById(R.id.ccl_fab_add_cc);

        fabNewCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCCIntent = new Intent(getActivity(), AddCreditCardActivity.class);
                startActivity(addCCIntent);
            }
        });
    }


    public void refreshData() throws CreditCardNotFoundException, CreditPeriodNotFoundException {
        activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of creditCardExpenses object when overwriting
        // activeCreditCard.getCreditPeriods().get(0).getExpenses();
        creditCardExpenses.clear();
        creditCardExpenses.addAll(activeCreditCard.getCreditPeriods().get(0).getExpenses());


    }


    public void refreshRecyclerView() {
        loadDao();

        int oldExpensesCount = creditCardExpenses.size();
        try {
            refreshData();
        }catch (CreditCardNotFoundException e ) {
            Toast.makeText(getActivity(), "Sorry, there was a problem loading the Credit Card", Toast.LENGTH_SHORT).show();
            return;
        }catch (CreditPeriodNotFoundException e) {
            Toast.makeText(getActivity(), "Sorry, there was a problem loading the Credit Period", Toast.LENGTH_SHORT).show();
            return;
        }

        int newExpensesCount = creditCardExpenses.size();

        //TODO: in the future, expenses wont necessarily be added with date=now,
        //TODO: meaning they wont always be added on recyclerview position = 0
        //If a new expense was added
        if(newExpensesCount == oldExpensesCount+1) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, activeCreditCard.getCreditPeriods().get(0).getExpenses().size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

//    private void refreshChart(){
//        //TODO: this is probably a hack.. maybe a listener is needed here?
//        //Refresh chartFragment
//        if(chartFragment != null)
//            chartFragment.refreshData();
//        else
//            Toast.makeText(getActivity(), "Error on onDismiss, chartFragment == null!", Toast.LENGTH_SHORT).show();
//    }

    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateOrEditExpenseDialogFragment dialog = CreateOrEditExpenseDialogFragment.newInstance(
                dao,
                activeCreditCard.getCreditPeriods().get(0).getId(),
                activeCreditCard.getCurrency(),
                null);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();
                //refreshChart();
            }
        });
        dialog.show(fm, "fragment_dialog_create_expense");
    }


}
