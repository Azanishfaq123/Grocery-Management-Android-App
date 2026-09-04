package nssoftwares.com.storemanagment.ui.home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nssoftwares.com.storemanagment.Connection.ConnectionClass;
import nssoftwares.com.storemanagment.Connection.ImageSliderAdapter;
import nssoftwares.com.storemanagment.ItemActivity;
import nssoftwares.com.storemanagment.R;
import nssoftwares.com.storemanagment.RecipiesListActivity;
import nssoftwares.com.storemanagment.Stock;
import nssoftwares.com.storemanagment.SuggestMeActivity;
import nssoftwares.com.storemanagment.databinding.FragmentHomeBinding;
import nssoftwares.com.storemanagment.recipiesactivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager2 imageSlider;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private List<ItemDetails> lowStockItems = new ArrayList<>(); // List to hold low stock items

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        requestNotificationPermission();

        imageSlider = binding.imageSlider;

        List<Integer> images = Arrays.asList(
                R.drawable.banner,
                R.drawable.banner2,
                R.drawable.banner3
        );

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        imageSlider.setAdapter(adapter);

        Button buttonAddItem = binding.buttonAddItem;
        Button buttonAddRecipes = binding.buttonAddRecipes;
        Button btnStockadd = binding.buttonstock;
        Button btnExisingrecipies = binding.btnPrintAddedRecipies;
        Button btnSuggestme = binding.buttonsuggestme;


        Button buttonDownloadList = binding.buttonDownloadList; // Add download list button

        buttonAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ItemActivity.class);
            startActivity(intent);
        });

        buttonAddRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), recipiesactivity.class);
            startActivity(intent);
        });

        btnStockadd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Stock.class);
            startActivity(intent);
        });
        btnExisingrecipies.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecipiesListActivity.class);
            startActivity(intent);
        });


        buttonDownloadList.setOnClickListener(v -> downloadLowStockList()); // Download list button click

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = imageSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % adapter.getItemCount();
                imageSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 5000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 5000);

        new Thread(this::checkLowStock).start(); // Execute in background thread

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    public void checkLowStock() {
        Connection connection = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
        if (connection != null) {
            try {
                String queryStock = "SELECT ItemCode, SUM(Qty) AS TotalQty FROM Stock GROUP BY ItemCode";
                Statement statement = connection.createStatement();
                ResultSet resultSetStock = statement.executeQuery(queryStock);

                List<StockItem> stockItems = new ArrayList<>();

                while (resultSetStock.next()) {
                    String itemCode = resultSetStock.getString("ItemCode");
                    int totalQty = resultSetStock.getInt("TotalQty");
                    stockItems.add(new StockItem(itemCode, totalQty));
                }

                resultSetStock.close();
                statement.close();

                String queryItem = "SELECT MinQty, ItemName FROM Itemstbl WHERE ItemCode = ?";
                PreparedStatement itemStatement = connection.prepareStatement(queryItem);

                for (StockItem stockItem : stockItems) {
                    itemStatement.setString(1, stockItem.getItemCode());

                    ResultSet resultSetItem = itemStatement.executeQuery();

                    if (resultSetItem.next()) {
                        int minQty = resultSetItem.getInt("MinQty");
                        String itemName = resultSetItem.getString("ItemName");

                        if (stockItem.getTotalQty() < minQty) {
                            lowStockItems.add(new ItemDetails(stockItem.getItemCode(), minQty, itemName));
                            new Handler(Looper.getMainLooper()).post(() -> sendLowStockNotification(itemName, stockItem.getItemCode()));
                        }
                    } else {
                        Log.e("SQL Error", "No data returned for ItemCode: " + stockItem.getItemCode());
                    }

                    resultSetItem.close();
                }

                itemStatement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("SQL Error", e.getMessage());
            }
        }
    }

    // Method to send low stock notifications
    private void sendLowStockNotification(String itemName, String itemCode) {
        String channelId = "low_stock_channel";
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NotificationManager.class);

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Low Stock Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(R.drawable.images) // Replace with your notification icon
                .setContentTitle("Low Stock Alert")
                .setContentText("Item " + itemName + " (Code: " + itemCode + ") is low in stock.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Download the low stock list as a PDF
    private void downloadLowStockList() {
        if (lowStockItems.isEmpty()) {
            Toast.makeText(getContext(), "No low stock items to download.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the PDF layout
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View pdfView = inflater.inflate(R.layout.pdf_layout, null);

        if (pdfView == null) {
            Log.e("PDF Error", "Failed to inflate pdf_layout.xml");
            Toast.makeText(getContext(), "Error creating PDF layout.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fill data into the layout
        TextView pdfTitle = pdfView.findViewById(R.id.pdfTitle);
        TextView pdfContent = pdfView.findViewById(R.id.pdfContent);

        if (pdfTitle == null || pdfContent == null) {
            Log.e("PDF Error", "TextView(s) not found in pdf_layout.xml");
            Toast.makeText(getContext(), "Error finding TextViews in PDF layout.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        for (ItemDetails item : lowStockItems) {
            content.append("Item Code: ").append(item.getItemCode()).append("\n")
                    .append("Item Name: ").append(item.getItemName()).append("\n\n");
        }

        pdfTitle.setText("Low Stock Report");
        pdfContent.setText(content.toString());

        // Measure and layout the PDF view
        pdfView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        pdfView.layout(0, 0, pdfView.getMeasuredWidth(), pdfView.getMeasuredHeight());

        // Convert the layout into a Bitmap
        Bitmap bitmap = getBitmapFromView(pdfView);
        if (bitmap == null) {
            Log.e("PDF Error", "Failed to create bitmap from view.");
            Toast.makeText(getContext(), "Error generating PDF content.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate and save the PDF in Downloads folder
        FileOutputStream out = null;
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                throw new IOException("Failed to create Downloads directory: " + downloadsDir.getPath());
            }

            File pdfFile = new File(downloadsDir, "Low_Stock_Report.pdf");

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            out = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(out);
            pdfDocument.close();

            Toast.makeText(getContext(), "PDF saved to Downloads folder: " + pdfFile.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("PDF Error", "Error writing PDF: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to save PDF. Please check storage permissions.", Toast.LENGTH_SHORT).show();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("PDF Error", "Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException | ClassNotFoundException e) {
            Log.e("Database Connection", e.getMessage());
        }

        return connection;
    }

    // Helper method to create a bitmap from a view
    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // Request notification permission for Android 13+
    private void requestNotificationPermission() {
        String channelId = "low_stock_channel";
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Low Stock Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for low stock items");
            channel.enableLights(true);
            channel.enableVibration(true); // Enable vibration if needed
            channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null); // Add default sound
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), channelId);
    }

    public void EXISTINGRECIPIES(View view) {

    }

    public void suggestMe(View view) {
        Intent intent = new Intent(getActivity(), SuggestMeActivity.class);
        startActivity(intent);
    }

    private static class StockItem {
        private String itemCode;
        private int totalQty;

        public StockItem(String itemCode, int totalQty) {
            this.itemCode = itemCode;
            this.totalQty = totalQty;
        }

        public String getItemCode() {
            return itemCode;
        }

        public int getTotalQty() {
            return totalQty;
        }
    }

    private static class ItemDetails {
        private String itemCode;
        private int minQty;
        private String itemName;

        public ItemDetails(String itemCode, int minQty, String itemName) {
            this.itemCode = itemCode;
            this.minQty = minQty;
            this.itemName = itemName;
        }

        public String getItemCode() {
            return itemCode;
        }

        public int getMinQty() {
            return minQty;
        }

        public String getItemName() {
            return itemName;
        }
    }
}
