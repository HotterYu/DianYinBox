/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Development\\code\\DianYinBox\\PushModel\\src\\main\\aidl\\com\\znt\\push\\IPushAidlInterface.aidl
 */
package com.znt.push;
public interface IPushAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.znt.push.IPushAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.znt.push.IPushAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.znt.push.IPushAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.znt.push.IPushAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.znt.push.IPushAidlInterface))) {
return ((com.znt.push.IPushAidlInterface)iin);
}
return new com.znt.push.IPushAidlInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setRequestParams:
{
data.enforceInterface(DESCRIPTOR);
com.znt.lib.bean.MediaInfor _arg0;
if ((0!=data.readInt())) {
_arg0 = com.znt.lib.bean.MediaInfor.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
boolean _arg2;
_arg2 = (0!=data.readInt());
this.setRequestParams(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_updatePlayRecord:
{
data.enforceInterface(DESCRIPTOR);
com.znt.lib.bean.MediaInfor _arg0;
if ((0!=data.readInt())) {
_arg0 = com.znt.lib.bean.MediaInfor.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updatePlayRecord(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateVolumeSetStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.updateVolumeSetStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDevId:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDevId();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurPlayMedia:
{
data.enforceInterface(DESCRIPTOR);
com.znt.lib.bean.MediaInfor _result = this.getCurPlayMedia();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getCurTimeingAd:
{
data.enforceInterface(DESCRIPTOR);
com.znt.lib.bean.MediaInfor _result = this.getCurTimeingAd();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getCurTimeInternalAd:
{
data.enforceInterface(DESCRIPTOR);
com.znt.lib.bean.MediaInfor _result = this.getCurTimeInternalAd();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPushMedias:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.znt.lib.bean.MediaInfor> _result = this.getPushMedias();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getCurPlayMedias:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.znt.lib.bean.MediaInfor> _result = this.getCurPlayMedias();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_init:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.init(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateProcessByTime:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.updateProcessByTime(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurServerTime:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getCurServerTime();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.znt.push.IPushCallback _arg0;
_arg0 = com.znt.push.IPushCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.znt.push.IPushCallback _arg0;
_arg0 = com.znt.push.IPushCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.znt.push.IPushAidlInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void setRequestParams(com.znt.lib.bean.MediaInfor mediaInfor, java.lang.String fnetInfo, boolean updateNow) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((mediaInfor!=null)) {
_data.writeInt(1);
mediaInfor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(fnetInfo);
_data.writeInt(((updateNow)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setRequestParams, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updatePlayRecord(com.znt.lib.bean.MediaInfor mediaInfor) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((mediaInfor!=null)) {
_data.writeInt(1);
mediaInfor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updatePlayRecord, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateVolumeSetStatus(boolean result) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((result)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateVolumeSetStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getDevId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDevId, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.znt.lib.bean.MediaInfor getCurPlayMedia() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.znt.lib.bean.MediaInfor _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurPlayMedia, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.znt.lib.bean.MediaInfor.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*List<MediaInfor> getCurPlayList();*/
@Override public com.znt.lib.bean.MediaInfor getCurTimeingAd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.znt.lib.bean.MediaInfor _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurTimeingAd, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.znt.lib.bean.MediaInfor.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.znt.lib.bean.MediaInfor getCurTimeInternalAd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.znt.lib.bean.MediaInfor _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurTimeInternalAd, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.znt.lib.bean.MediaInfor.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<com.znt.lib.bean.MediaInfor> getPushMedias() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.znt.lib.bean.MediaInfor> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPushMedias, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.znt.lib.bean.MediaInfor.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<com.znt.lib.bean.MediaInfor> getCurPlayMedias() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.znt.lib.bean.MediaInfor> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurPlayMedias, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.znt.lib.bean.MediaInfor.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void init(boolean isPlugin) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isPlugin)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_init, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateProcessByTime(long time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(time);
mRemote.transact(Stub.TRANSACTION_updateProcessByTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public long getCurServerTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurServerTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void registerCallback(com.znt.push.IPushCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.znt.push.IPushCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setRequestParams = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_updatePlayRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_updateVolumeSetStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getDevId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getCurPlayMedia = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getCurTimeingAd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getCurTimeInternalAd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getPushMedias = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getCurPlayMedias = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_updateProcessByTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getCurServerTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
public void setRequestParams(com.znt.lib.bean.MediaInfor mediaInfor, java.lang.String fnetInfo, boolean updateNow) throws android.os.RemoteException;
public void updatePlayRecord(com.znt.lib.bean.MediaInfor mediaInfor) throws android.os.RemoteException;
public void updateVolumeSetStatus(boolean result) throws android.os.RemoteException;
public java.lang.String getDevId() throws android.os.RemoteException;
public com.znt.lib.bean.MediaInfor getCurPlayMedia() throws android.os.RemoteException;
/*List<MediaInfor> getCurPlayList();*/
public com.znt.lib.bean.MediaInfor getCurTimeingAd() throws android.os.RemoteException;
public com.znt.lib.bean.MediaInfor getCurTimeInternalAd() throws android.os.RemoteException;
public java.util.List<com.znt.lib.bean.MediaInfor> getPushMedias() throws android.os.RemoteException;
public java.util.List<com.znt.lib.bean.MediaInfor> getCurPlayMedias() throws android.os.RemoteException;
public void init(boolean isPlugin) throws android.os.RemoteException;
public void updateProcessByTime(long time) throws android.os.RemoteException;
public long getCurServerTime() throws android.os.RemoteException;
public void registerCallback(com.znt.push.IPushCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.znt.push.IPushCallback cb) throws android.os.RemoteException;
}
