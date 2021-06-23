package com.galeopsis.contentprovidertest.contacts.listener

interface OnCallListener<T> {

    fun onCall(t: T)

    fun onMessage(t: T)
}