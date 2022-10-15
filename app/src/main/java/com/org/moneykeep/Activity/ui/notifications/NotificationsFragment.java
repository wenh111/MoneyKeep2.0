package com.org.moneykeep.Activity.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.org.moneykeep.Activity.ForgetPasswordView.AuthenticationView.ForgetPasswordActivity;
import com.org.moneykeep.Activity.OCRView.OCRActivity;
import com.org.moneykeep.Activity.SignInView.SignInActivity;
import com.org.moneykeep.Dialog.UpdateRemarkDialog;
import com.org.moneykeep.R;
import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.databinding.FragmentNotificationsBinding;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button but_log_out, but_update_password;
    public String USER_EMAIL = "user_email";
    public String USER_PASSWORD = "user_password";
    public String USER_ISUSED = "user_isused";
    public String USER_ID = "user_objectId";
    public String USER_NAME = "user_name";
    private TextView user_name, user_account_input;
    private String bundle_user_name;
    private String bundle_user_email;
    private int bundle_user_id;
    private ImageView user_icon;
    private UserPhotoAPI api;


    private final ActivityResultLauncher<Intent> intentActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    /*ActivityResultLauncher<Intent> intentPictureResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),this::onPictureResult);

    private  void onPictureResult(ActivityResult result) {
        if(result.getResultCode() == Activity.RESULT_OK){

        }
    }*/
    /*private boolean needDownload = true;*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /*NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);*/

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        api = RetrofitUntil.getRetrofit().create(UserPhotoAPI.class);
        View root = binding.getRoot();
        SharedPreferences controllerdata = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        /*Bundle receive = requireActivity().getIntent().getExtras();*/
        bundle_user_name = controllerdata.getString("user_name","");
        bundle_user_email = controllerdata.getString("user_email","");
        bundle_user_id = controllerdata.getInt("user_objectId",0);

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FindId();
        SetListen();

        user_name.setText(bundle_user_name);
        user_account_input.setText(bundle_user_email);

       /* SharedPreferences uridata = requireActivity().getSharedPreferences("uri", Context.MODE_PRIVATE);
        needDownload = uridata.getBoolean("needDownload", true);*/
        int REQUEST_CODE_CONTACT = 101;
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        //验证是否许可权限
        for (String str : permissions) {
            if (requireActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                requireActivity().requestPermissions(permissions, REQUEST_CODE_CONTACT);
                return;
            }
        }
        setUserPhoto();
        /*if (needDownload) {
            setUserPhoto();
        } else {
            String uri_path = uridata.getString("uripath", "");
            //Uri filepath = Uri.fromFile(new File(uri_path));
            if (!uri_path.equals("")) {
                if(!fileIsExists(uri_path)){
                    setUserPhoto();
                }else{
                    Log.i(TAG, "uri_path =================>" + uri_path);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), uri_path);
                    //RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(25);
                    user_icon.setImageDrawable(roundedBitmapDrawable);
                    //uri = Uri.fromFile(new File(uri_path));
                    //user_icon.setImageURI(filepath);
                }

            }
        }*/


    }

    private void setUserPhoto() {
        /*Call<ResponseBody> responseBodyCall = api.downPhoto(bundle_user_email);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    String fileName = "未命名.png";
                    Headers headers = response.headers();
                    String fileNameHeader = headers.get("Content-Disposition");
                    if (fileNameHeader != null) {
                        fileName = fileNameHeader.replace("attachment;filename=", "");
                        Log.i(TAG, fileName);
                    }
                    writString2Disk(response, fileName);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(),"头像获取失败...",Toast.LENGTH_SHORT).show();
            }
        });*/
        Call<UploadSuccessfulMessage> uploadSuccessfulMessageCall = api.getPhotoUrl(bundle_user_email);
        uploadSuccessfulMessageCall.enqueue(new Callback<UploadSuccessfulMessage>() {
            @Override
            public void onResponse(@NonNull Call<UploadSuccessfulMessage> call, @NonNull Response<UploadSuccessfulMessage> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    if (Objects.requireNonNull(response.body()).getUrl() != null) {
                        RoundedCorners roundedCorners = new RoundedCorners(20);
                        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                        // RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(20, 20);
                        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
                        Glide.with(requireActivity()).load(response.body().getUrl()).apply(options).into(user_icon);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadSuccessfulMessage> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "头像获取失败...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void writString2Disk(Response<ResponseBody> response, String fileName) {
        new Thread(() -> {

            assert response.body() != null;
            InputStream inputStream = response.body().byteStream();
            File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.i(TAG, "externalFilesDir ================>" + externalFilesDir);

            //user_icon.post(() -> user_icon.setImageDrawable(roundedBitmapDrawable));
            String uri = externalFilesDir + "/" + fileName;
            boolean isExists = fileIsExists(uri);
            File outFile = new File(externalFilesDir, fileName);
            Log.i(TAG, "uri ================>" + uri);
            SharedPreferences uridata = getActivity().getSharedPreferences("uri", Context.MODE_PRIVATE);
            SharedPreferences.Editor uri_editor = uridata.edit();
            FileOutputStream fileOutputStream = null;
            try {
                if (!isExists) {
                    Log.i(TAG, "写入文件...");
                    fileOutputStream = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    Log.i(TAG, "写入完成...");
                }
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), uri);
                roundedBitmapDrawable.setCornerRadius(25);
                user_icon.post(() -> user_icon.setImageDrawable(roundedBitmapDrawable));
                //Uri filepath = Uri.fromFile(outFile);
               *//* RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), uri);
                roundedBitmapDrawable.setCornerRadius(25);*//*
                //Uri filepath = Uri.fromFile(outFile);
                //user_icon.post(() -> user_icon.setImageURI(filepath));
                //user_icon.post(() -> user_icon.setImageDrawable(roundedBitmapDrawable));
                uri_editor.putString("uripath", uri);
                uri_editor.putBoolean("needDownload", false);
                uri_editor.apply();
                //Log.i(TAG, "filepath ================>" + filepath);
                Log.i(TAG, "uripath ================>" + uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri) {
        String path = null;
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void SetListen() {
        Onclick onclick = new Onclick();
        but_log_out.setOnClickListener(onclick);
        user_icon.setOnClickListener(onclick);
        but_update_password.setOnClickListener(onclick);
        binding.updateName.setOnClickListener(onclick);
        binding.butOcr.setOnClickListener(onclick);
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
            Intent resultData = result.getData();
            Uri uri = resultData.getData();
            //SharedPreferences uridata = getActivity().getSharedPreferences("uri", MODE_PRIVATE);
            try {
                String mpath = getImagePath(uri);
                /*SharedPreferences.Editor uri_editor = uridata.edit();*/
                Log.i("mpath ===>", mpath);
                File file = new File(mpath);
                RequestBody body = RequestBody.create(file, MediaType.parse("image/jpeg"));
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);

                Call<UploadSuccessfulMessage> stringCall = api.UploadPhoto(part, bundle_user_email);
                stringCall.enqueue(new Callback<UploadSuccessfulMessage>() {
                    @Override
                    public void onResponse(@NonNull Call<UploadSuccessfulMessage> call, @NonNull Response<UploadSuccessfulMessage> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            Log.i("uploadSuccessful", "============>" + (response.body() != null ? response.body().getUrl() : null));
                           /* uri_editor.putString("uripath", "");
                            uri_editor.putBoolean("needDownload", true);
                            uri_editor.apply();*/
                            setUserPhoto();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UploadSuccessfulMessage> call, @NonNull Throwable t) {
                        Log.i("uploadUnSuccessful", "============>" + t.getMessage());
                        Toast.makeText(getContext(), "头像上传服务器失败:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                /*ContentResolver cr = getActivity().getContentResolver();
                 *//* 将Bitmap设定到ImageView *//*
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                user_icon.setImageBitmap(bitmap);*/
                /*RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), mpath);
                roundedBitmapDrawable.setCornerRadius(25);
                user_icon.setImageDrawable(roundedBitmapDrawable);*/
            } catch (IllegalStateException e) {
                Toast.makeText(getContext(), "错误" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            Log.i("photoViewCallBack", "没有切换头像");
        }

    }

    private class Onclick implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.but_log_out:
                    Logout();
                    break;
                case R.id.user_icon:
                    ChangeIcon();
                    break;
                case R.id.but_update_password:
                    Intent intent = new Intent(getContext(), ForgetPasswordActivity.class);
                    startActivity(intent);
                    break;
                case R.id.update_name:
                    UpdateRemarkDialog updateNameDialog = new UpdateRemarkDialog(requireContext());
                    updateNameDialog.setOldRemark(user_name.getText().toString());
                    updateNameDialog.setiOconfirmListener((dialog, newName) -> {
                        if (newName == null || newName.equals(user_name.getText().toString())) {
                            Toast.makeText(requireContext(), "请填入新名字...", Toast.LENGTH_LONG).show();
                        } else {
                            Call<Integer> integerCall = api.updateName(bundle_user_id, newName);
                            integerCall.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(@NonNull Call<Integer> call,@NonNull Response<Integer> response) {
                                    SharedPreferences controllerdata = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                                    SharedPreferences.Editor controller_editor = controllerdata.edit();
                                    controller_editor.putString("user_name", newName);
                                    controller_editor.apply();
                                    user_name.setText(newName);
                                    Toast.makeText(requireContext(), "名字更改成功...", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<Integer> call,@NonNull Throwable t) {
                                    Toast.makeText(requireContext(), "名字更改失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).show();
                    break;
                case R.id.but_ocr:
                    Intent OCRiIntent = new Intent(requireContext(), OCRActivity.class);
                    startActivity(OCRiIntent);
                    //choosePicture();
                    break;
            }
        }
    }

    /*private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intentPictureResultLauncher.launch(intent);
        //startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }*/

    private void ChangeIcon() {
        Intent intent = new Intent();
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intentActivityResultLauncher.launch(intent);
    }

    private void Logout() {
        SharedPreferences controllerdata = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor controller_editor = controllerdata.edit();
        controller_editor.putString(USER_NAME, "");
        controller_editor.putString(USER_EMAIL, "");
        controller_editor.putString(USER_PASSWORD, "");
        controller_editor.putBoolean(USER_ISUSED, false);
        controller_editor.putInt(USER_ID, 0);
        controller_editor.apply();
        SharedPreferences uridata = requireActivity().getSharedPreferences("uri", Context.MODE_PRIVATE);
        SharedPreferences.Editor uri_editor = uridata.edit();
        uri_editor.putString("uripath", "");
        uri_editor.putBoolean("needDownload", true);
        uri_editor.apply();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void FindId() {
        but_update_password = binding.butUpdatePassword;/*getView().findViewById(R.id.but_update_password)*/
        user_icon = binding.userIcon;/*getView().findViewById(R.id.user_icon)*/
        but_log_out = binding.butLogOut;/*getView().findViewById(R.id.but_log_out)*/
        user_name = binding.userName;/*getView().findViewById(R.id.user_name)*/
        user_account_input = binding.userAccountInput;/*getView().findViewById(R.id.user_account_input)*/
    }

    //fileName 为文件名称 返回true为存在
    /*public boolean fileIsExists(String fileName) {
        String TAG = "PhotoMessage";
        try {
            File f = new File(fileName);
            if (f.exists()) {
                Log.i(TAG, "fileIsExists =============> " + "有这个文件");
                return true;
            } else {
                Log.i(TAG, "fileIsExists =============> " + "没有这个文件");
                return false;
            }
        } catch (Exception e) {
            Log.i(TAG, "fileIsExists =============> " + "崩溃");
            return false;
        }
    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}