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

namespace OpenPeerSampleAppCSharp
{
	[Activity (Label = "Open Peer Sample App - Chat", WindowSoftInputMode = SoftInput.AdjustPan)]			
	public class ChatActivity : ListActivity,
								View.IOnKeyListener
	{
		private EditText editText;

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// Create your application here
			SetContentView (Resource.Layout.Chat);

			this.ListAdapter = new ChatAdapter (this);

			ListView listView = FindViewById<ListView> (Android.Resource.Id.List);
			listView.ItemsCanFocus = true;

			editText = FindViewById<EditText> (Resource.Id.editText);

			Button sendButton = FindViewById<Button> (Resource.Id.sendButton);

			listView.Touch += (object sender, View.TouchEventArgs e) => {
				Console.WriteLine ("touch event");
				ClearEditFocus ();
			};

			sendButton.Click += (object sender, EventArgs e) => {
				OnSend ();
			};

			editText.SetOnKeyListener (this);
		}

		protected override void OnStop()
		{
			base.OnStop ();
			Console.WriteLine ("stopping");
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
				Console.WriteLine ("entered");
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
				Console.WriteLine("focus cleared");
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
			Console.WriteLine("send!!");

			editText.Text = "";
			editText.RequestFocus ();
		}
	}
}
