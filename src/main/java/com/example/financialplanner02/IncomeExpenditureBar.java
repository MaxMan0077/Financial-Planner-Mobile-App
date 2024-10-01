package com.example.financialplanner02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class IncomeExpenditureBar extends View {
    private double income;
    private double expenditure;

    private Paint incomePaint;
    private Paint expenditurePaint;

    public IncomeExpenditureBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        incomePaint = new Paint();
        incomePaint.setColor(context.getResources().getColor(R.color.incomeColor, null));

        expenditurePaint = new Paint();
        expenditurePaint.setColor(context.getResources().getColor(R.color.expenditureColor, null));
    }

    public void setValues(double income, double expenditure) {
        this.income = income;
        this.expenditure = expenditure;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double total = income + expenditure;
        int incomeWidth = (int) ((income / total) * getWidth());
        int expenditureWidth = getWidth() - incomeWidth;

        canvas.drawRect(0, 0, incomeWidth, getHeight(), incomePaint);
        canvas.drawRect(incomeWidth, 0, incomeWidth + expenditureWidth, getHeight(), expenditurePaint);
    }
}

