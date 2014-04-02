using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Views.InputMethods;
using Android.Text.Method;
using HopSampleApp.Services;
using OpenPeerSdk.Helpers;

namespace HopSampleApp
{
	namespace Activities
	{
		[LoggerSubsystem("hop_sample_app")]
		[Activity (Label = "Open Peer Sample App - Chat", WindowSoftInputMode = SoftInput.AdjustPan)]			
		public class ChatActivity : ListActivity,
									View.IOnKeyListener
		{
			private ImageCachingServiceDownloader downloader = new ImageCachingServiceDownloader ();
			private EditText editText;
			private ServiceConnection<Services.ImageCachingService> imageCachingServiceConnection;

			protected override void OnCreate (Bundle bundle)
			{
				base.OnCreate (bundle);

				// Create your application here
				SetContentView (Resource.Layout.Chat);

				imageCachingServiceConnection = ServiceConnection<Services.ImageCachingService>.Bind (this, downloader);

				this.ListAdapter = new ChatAdapter (this, downloader);

				ListView listView = FindViewById<ListView> (Android.Resource.Id.List);
				listView.ItemsCanFocus = true;

				editText = FindViewById<EditText> (Resource.Id.editText);

				Button sendButton = FindViewById<Button> (Resource.Id.sendButton);

				listView.Touch += (object sender, View.TouchEventArgs e) => {
					Logger.Trace ("touch event");
					ClearEditFocus ();
				};

				sendButton.Click += (object sender, EventArgs e) => {
					OnSend ();
				};

				editText.SetOnKeyListener (this);
			}

			protected override void OnDestroy ()
			{
				if (imageCachingServiceConnection != null) {
					imageCachingServiceConnection.Dispose ();
					imageCachingServiceConnection = null;
				}

				base.OnDestroy ();
			}

			public override void OnBackPressed ()
			{
				// prevent back button during login process
				Finish ();
				this.OverridePendingTransition (Resource.Animation.SlideInLeft, Resource.Animation.SlideOutRight);
			}

			bool View.IOnKeyListener.OnKey (View v, Keycode keyCode, KeyEvent e)
			{
				if (e.Action == KeyEventActions.Down && keyCode == Keycode.Enter) {
					Logger.Debug ("entered pressed");
					OnSend ();
					return true;
				}
				return false;
			}

			protected void ClearEditFocus()
			{
				if (editText.HasFocus) {
					CloseSoftInput ();
					editText.ClearFocus ();
					Logger.Debug("focus cleared");
				}
			}

			protected void CloseSoftInput()
			{
				View currentView = this.CurrentFocus;
				if (currentView == null)
					return;

				InputMethodManager inputManager = 
					(InputMethodManager)GetSystemService (InputMethodService);

				inputManager.HideSoftInputFromWindow(
					currentView.WindowToken,
					HideSoftInputFlags.NotAlways); 
			}

			protected void OnSend()
			{
				Logger.Debug("send clicked");

				editText.Text = "";
				editText.RequestFocus ();
			}
		}
	}
}

