package russia.com.baitapnhom131;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Users extends AppCompatActivity {

    //
    // khai báo các thành phần của giao diện
    // ListView usersList để hiển thị danh sách người dùng
    ListView usersList;
    // Trong trường hợp không thấy người dùng trong db thì dòng text này sẽ thông báo
    TextView noUsersText;
    // sử dụng ArrayList có tên al để lưu lại người dùng nào được chọn khi click vào một người trong ListView
    ArrayList<String> al = new ArrayList<>();
    // Hộp thoại ProgressDialog để hiển thị quá trình sử lý của chương trình
    ProgressDialog pd;
    // khởi tạo số lượng người Chat ban đầu là 0
    int totalUsers = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // connect đến giao diện
        setContentView(R.layout.activity_users);
        // connect từng phần tử giao diện đã khai báo ở trên đến giao diện xml
        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);
        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading ...");
        pd.show();

        // đường dẫn url để kết nối với database firebase, truyền dữ liệu dưới dạng json

        String url = "https://baitapnhom131.firebaseio.com/users.json";

        // ta sử dụng thư viện Volley để thực hiện các câu truy vấn qua mạng Internet, giúp cho tốc độ chương trình nhanh hơn
        // StringRequest là một request class đã được viết sẵn trong thư viện này để thực thi truy vấn

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            // nếu như cấu truy vấn thực hiện thành công thì ta sẽ thực thi hàm doOnSuccess(s)
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            // nếu không thực hiện thành công thì thông báo lỗi trả về
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("" + error);
            }
        });

        // việc giao tiếp với Network được quản lý bỏi RequestQueue, tạo ra một cache giúp cho việc reload lại dữ liệu nhanh hơn
        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);

        // sử lý sự kiện khi một người dùng trong danh sách được chọn
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                // khỏi tạo Activity, truyền dữ liệu giữa các Intent
                startActivity(new Intent(Users.this, Chat.class));
            }
        });
    }

    // hàm doOnSuccess thực hiện tác vụ khi gửi truy vấn thành công
    private void doOnSuccess(String s) {
        try {
            // JSON là viết tắt của JavaScript Object Notation. Nó là một định dạng trao đổi dữ liệu độc lập và là giải pháp thay thế tốt nhất cho XML
            // sử dụng lớp JSONObject để thao tác dữ liệu JSON
            JSONObject obj = new JSONObject(s);
            // sử dụng Iterator để duyệt từ đầu đến cuối của list keys
            Iterator i = obj.keys();
            String key;

            while (i.hasNext()) {
                key = i.next().toString();
                if (!key.equals(UserDetails.username)) {
                    al.add(key);
                }
                totalUsers++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            // nếu như có người dùng trong database thì load vào trong danh sách
            usersList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, al));
        }
    }

}
