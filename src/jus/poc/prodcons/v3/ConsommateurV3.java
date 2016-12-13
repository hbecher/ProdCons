package jus.poc.prodcons.v3;

import static jus.poc.prodcons.message.MessageEnd.MESSAGE_END;
import static jus.poc.prodcons.options.Config.DEFAULT_CONFIG;

import jus.poc.prodcons.*;
import jus.poc.prodcons.message.MessageEnd;
import jus.poc.prodcons.print.Printer;

public class ConsommateurV3 extends Acteur implements _Consommateur
{
	private static final Aleatoire ALEATOIRE = new Aleatoire(DEFAULT_CONFIG.getConsTimeMean(), DEFAULT_CONFIG.getConsTimeDev());
	private final ProdConsV3 tampon;
	private int nombreMessages = 0;

	public ConsommateurV3(Observateur observateur, ProdConsV3 tampon) throws ControlException
	{
		super(Acteur.typeConsommateur, observateur, DEFAULT_CONFIG.getConsTimeMean(), DEFAULT_CONFIG.getConsTimeDev());

		this.tampon = tampon;

		observateur.newConsommateur(this);

		Printer.printNewConsumer(this);
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Message message = tampon.get(this);

				if(message == MESSAGE_END || message instanceof MessageEnd)
				{
					break;
				}

				int time = ALEATOIRE.next();

				Thread.sleep(time);

				observateur.consommationMessage(this, message, time);

				Printer.printConsumption(this, message, time);

				nombreMessages++;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		Printer.printEndConsumer(this);

		tampon.decConsumers();
	}

	@Override
	public int nombreDeMessages()
	{
		return nombreMessages;
	}
}
