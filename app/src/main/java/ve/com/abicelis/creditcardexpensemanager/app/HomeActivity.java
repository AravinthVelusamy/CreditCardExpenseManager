package ve.com.abicelis.creditcardexpensemanager.app;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapter.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateExpenseDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.mocks.CreditMock;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnDismissListener {

    //Data
    List<CreditCard> creditCards = new ArrayList<>();
    List<CreditPeriod> creditPeriods = new ArrayList<>();
    List<Expense> expenses = new ArrayList<>();
    List<Payment> payments = new ArrayList<>();
    ExpenseManagerDAO dao;

    //UI
    RecyclerView recyclerViewExpenses;
    ExpensesAdapter adapter;
    Toolbar toolbar;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabNewExpense;
    FloatingActionButton fabNewExpenseCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                refreshData();
                setUpExpensesRecyclerView();
            }
        };
        handler.postDelayed(r, 1000);



        //setUpExpensesRecyclerView();
        setUpToolbar();
        setUpFab();
    }

    private void refreshData() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getApplicationContext());

        creditCards.clear();
        creditCards.addAll(dao.getCreditCardList());

        creditPeriods.clear();
        creditPeriods.addAll(dao.getCreditPeriodListFromCard(creditCards.get(0).getId()));

        expenses.clear();
        expenses.addAll(dao.getExpensesFromCreditPeriod(creditPeriods.get(0).getId()));

        payments.clear();
        payments.addAll(dao.getPaymentsFromCreditPeriod(creditPeriods.get(0).getId()));
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.home_fab_new_expense:
                fabMenu.close(true);
                showCreateExpenseDialog();
                break;
            case R.id.home_fab_new_expense_camera:
                Toast.makeText(HomeActivity.this, "Not yet implemented!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void showCreateExpenseDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CreateExpenseDialogFragment dialog = CreateExpenseDialogFragment.newInstance(getResources().getString(R.string.dialog_create_expense_title), dao);
        dialog.show(fm, "fragment_dialog_create_expense");
    }





    private void setUpExpensesRecyclerView() {

        recyclerViewExpenses = (RecyclerView) findViewById(R.id.home_recycler_expenses);

        adapter = new ExpensesAdapter(getApplicationContext(), expenses);
        recyclerViewExpenses.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewExpenses.setLayoutManager(layoutManager);
    }

    private void setUpToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.home_appbar);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work

                    isShow = false;
                }
            }
        });
    }

    private void setUpFab() {
        fabMenu = (FloatingActionMenu) findViewById(R.id.home_fab_menu);
        fabNewExpense = (FloatingActionButton) findViewById(R.id.home_fab_new_expense);
        fabNewExpenseCamera = (FloatingActionButton) findViewById(R.id.home_fab_new_expense_camera);

        fabNewExpense.setOnClickListener(this);
        fabNewExpenseCamera.setOnClickListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        refreshData();
        adapter.notifyItemInserted(expenses.size()-1);

        adapter.notifyDataSetChanged();
    }
}
