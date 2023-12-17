import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import android.widget.Toast

class PasswordAdapter(private val passwordItems: List<PasswordItem>) :
    RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passwordItem = passwordItems[position]

        val passwordText = "${passwordItem.name} - ${if (passwordItem.isPasswordVisible) passwordItem.value else encodePassword()}"

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
                Toast.makeText(holder.passwordTextView.context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
                true
            } else {
                // Password is not visible, do not copy
                false
            }
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
}
