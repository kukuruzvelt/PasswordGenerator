import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class PasswordAdapter(private val passwordItems: MutableList<PasswordItem>, private var token: String) :
    RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passwordItem = passwordItems[position]

        val passwordText =
            "${passwordItem.name} - ${if (passwordItem.isPasswordVisible) passwordItem.value else encodePassword()}"

        holder.passwordTextView.text = passwordText

        holder.passwordTextView.setOnClickListener {
            // Toggle visibility of password value
            passwordItem.isPasswordVisible = !passwordItem.isPasswordVisible
            notifyItemChanged(position)
        }

        // Inside onBindViewHolder method of PasswordAdapter
        holder.passwordTextView.setOnLongClickListener {
            if (passwordItem.isPasswordVisible) {
                // Copy password value to clipboard
                copyToClipboard(passwordItem.value, holder.passwordTextView.context)
                // Show a toast message
                Toast.makeText(
                    holder.passwordTextView.context,
                    "Password copied to clipboard",
                    Toast.LENGTH_SHORT
                ).show()
                true
            } else {
                // Password is not visible, do not copy
                false
            }
        }

        holder.deleteButton.setOnClickListener {
            // Add your delete logic here, e.g., remove the password item from the list and notify the adapter
            onPasswordDelete(passwordItem)  // Assuming you have a function to handle deletion
        }
    }

    override fun getItemCount(): Int {
        return passwordItems.size
    }

    private fun encodePassword(): String {
        // Implement your password encoding logic here
        // For simplicity, this example uses "********" as an encoded password
        return "********"
    }

    private fun copyToClipboard(text: String, context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Password", text)
        clipboard.setPrimaryClip(clip)

        // Show a toast message
        Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    // Placeholder for your delete logic
    private fun onPasswordDelete(passwordItem: PasswordItem) {
        val url = "http://10.0.2.2:8000/api/passwords/" + passwordItem.id

        // Use the application scope for the coroutine
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    val connectTimeout = 30000L // 30 seconds
                    val readTimeout = 30000L // 30 seconds

                    val client = OkHttpClient.Builder()
                        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .build()

                    // HTTP Request
                    val request = Request.Builder()
                        .url(url)
                        .delete()
                        .addHeader("accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + token)
                        .build()

                    // Use suspend function to execute the request asynchronously
                    client.newCall(request).execute()
                } catch (e: Exception) {
                    // Handle exceptions
                    Log.e("t", "Error deleting password", e)
                }
            }

            // Update UI or perform other actions on the main thread
            val position = passwordItems.indexOf(passwordItem)
            passwordItems.removeAt(position)
            notifyDataSetChanged() // You might need to notify the adapter about the data change
        }
    }

}
