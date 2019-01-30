package de.csicar.mensaplan

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MEAL = "de.csicar.mensaplan.meal"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MealPropertyFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MealPropertyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MealPropertyFragment : Fragment() {
    private var meal: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            meal = Gson().fromJson(it.getString(ARG_MEAL), Meal::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_properties, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MealPropertyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                MealPropertyFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MEAL, param1)
                    }
                }
    }
}
