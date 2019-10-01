import mtt
from os.path import join, realpath, dirname
from os import getcwd
from numpy import array, sqrt, mean
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
plt.style.use('seaborn')

sbfile = join(realpath(dirname(__file__)),'michaelis-menten-explicit-e.sb')

nrows = 2
ncols = 3
t_start = 0
t_stop = 100

import tellurium as te
with open(sbfile) as f:
    rr_model = te.loada(f.read())
rr_results = rr_model.simulate(t_start,t_stop,1000)
rr_time = array(rr_results[:,0])
rr_s = array(rr_results[:,2])
rr_p = array(rr_results[:,3])

fig,axes = plt.subplots(nrows, ncols, sharex='col', sharey='row', figsize=(15,8))

axes[0][0].set_title('Lumped Kinetics')
axes[0][0].plot(rr_time, rr_s, color='C1', linewidth=2, label='S (lumped)', linestyle='--')
axes[0][0].plot(rr_time, rr_p, color='C2', linewidth=2, label='P (lumped)', linestyle='--')
# axes[0][0].set_xlabel('time (a.u.)')
axes[0][0].set_ylabel('conc. (a.u.)')

for k,default_margin in enumerate([0.01, 0.1, 1., 10., 100.,],1):
    print('default_margin =',default_margin)
    wiring = mtt.Wiring.fromFile2(sbfile, {}, default_margin)
    model = mtt.MTT(wiring)
    model.reset()

    if default_margin > 10.:
        rr_model.reset()
        rr_results = rr_model.simulate(t_start,t_stop,10000)
        rr_time = array(rr_results[:,0])
        rr_s = array(rr_results[:,2])
        rr_p = array(rr_results[:,3])
    r = model.simulate(t_start,t_stop,1000 if default_margin <= 10. else 10000)

    mtt_time = array(r[:,0])
    mtt_s = array(r[:,2])
    mtt_p = array(r[:,4])

    rms = sqrt(mean([
        mean((mtt_s-rr_s)**2.),
        mean((mtt_p-rr_p)**2.),
    ]))
    row = int(k/ncols)
    col = k%ncols
    axis = axes[row][col]
    axis.set_title(r'$\rho / k_{cat} = '+str(default_margin/rr_model.kcat)+'$')
    if (row == nrows-1):
        axis.set_xlabel('time (a.u.)')
    if (col == 0):
        axis.set_ylabel('conc. (a.u.)')

    axis.plot(rr_time, rr_s, color='C1', linewidth=2, label='S (lumped)', linestyle='--')
    axis.plot(mtt_time, mtt_s, color='C1', linewidth=2, label='S')

    axis.plot(rr_time, rr_p, color='C2', linewidth=2, label='P (lumped)', linestyle='--')
    axis.plot(mtt_time, mtt_p, color='C2', linewidth=2, label='P')

    handles, labels = axis.get_legend_handles_labels()

fig.legend(handles, labels, loc='lower right')

plt.savefig(join(realpath(dirname(__file__)), 'margins-plot.pdf'), format='pdf')
columns = 2
model.mtt.renderDiagram(columns,join(realpath(dirname(__file__)), 'mm-explicit-diagram.png'))
