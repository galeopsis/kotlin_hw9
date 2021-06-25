package com.galeopsis.contentprovidertest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.galeopsis.contentprovidertest.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.detailContactText.text = intent.extras!!.getString("super_key")!!

    }
}