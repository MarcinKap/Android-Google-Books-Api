package com.example.androidgooglebooksapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.androidgooglebooksapi.views.bookList.BooksListFragment
import com.example.androidgooglebooksapi.models.bookList.BookList
import com.example.androidgooglebooksapi.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment()

    }

    private fun addFragment() {


//        supportFragmentManager.beginTransaction()
//            .replace(
//                R.id.container_fragment,
//                BooksListFragment(null)
//            )

        val getBookList = RetrofitInstance.getApiRepository.getNews()
        getBookList.enqueue(object : Callback<BookList> {
            override fun onResponse(call: Call<BookList>, response: Response<BookList>) {
                if (response.isSuccessful) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container_fragment,
                            BooksListFragment(response.body())
                        )
                        .commit()
                }
            }

            override fun onFailure(call: Call<BookList>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Problem", Toast.LENGTH_SHORT).show()
            }
        }

        )


    }


//
////                    setGoneErrorTextViews();
//        ApiUtils.getApiService()?.getNews()
//            ?.subscribeOn(Schedulers.io())
//            ?.observeOn(AndroidSchedulers.mainThread())
//            ?.subscribe(object : DisposableObserver<Response<BookList?>?>() {
//                override fun onNext(response: Response<BookList?>) {
//                    if (ApiUtils.getResponseStatusCode(response) === 200) {
//                        if (response.body() != null) {
//
//
//
//                        }
//
//
//                        val intent = Intent(getActivity(), MainActivity::class.java)
//                        startActivity(intent)
//                        //Close Notification Drawer because after clicked it doesn't close
//                        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
//                        context.sendBroadcast(closeIntent)
//                        getActivity().finish()
//                        //                                        newsService.saveNews(response.body());
//                        // włączanie fragmentu z newsami
//    //                                        runNewsFragment(response.body());
//                    }
//                }
//
//                override fun onError(e: Throwable) {
//                    Log.e("API_CALL", e.message, e)
//                }
//
//                override fun onComplete() {}
//            })


//


}